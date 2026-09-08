package com.example.loanbot.bot;

import com.example.loanbot.formatter.AnalyticsFormatter;
import com.example.loanbot.formatter.HistoryFormatter;
import com.example.loanbot.formatter.PaymentScheduleFormatter;
import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentScheduleItem;
import com.example.loanbot.model.PaymentType;
import com.example.loanbot.service.AnalyticsService;
import com.example.loanbot.service.LoanService;
import com.example.loanbot.service.ManagerAuthService;

import java.math.BigDecimal;
import java.util.List;

public class BotMessageHandler {

    private static final String MANAGER_ACCESS_DENIED =
            "Доступ только для менеджеров. Авторизуйтесь: /manager_login <логин> <пароль>";

    private static final String CALLBACK_CALCULATE = "CALC";
    private static final String CALLBACK_HISTORY = "HISTORY";
    private static final String CALLBACK_PAYMENT_TYPE_PREFIX = "PAYTYPE:";

    private final LoanService loanService;
    private final UserSessionStorage sessionStorage;
    private final PaymentScheduleFormatter scheduleFormatter;
    private final HistoryFormatter historyFormatter;
    private final AnalyticsService analyticsService;
    private final AnalyticsFormatter analyticsFormatter;
    private final ManagerAuthService managerAuthService;
    private final ManagerSessionStorage managerSessionStorage;

    public BotMessageHandler(LoanService loanService,
                             UserSessionStorage sessionStorage,
                             PaymentScheduleFormatter scheduleFormatter,
                             HistoryFormatter historyFormatter,
                             AnalyticsService analyticsService,
                             AnalyticsFormatter analyticsFormatter,
                             ManagerAuthService managerAuthService,
                             ManagerSessionStorage managerSessionStorage) {
        this.loanService = loanService;
        this.sessionStorage = sessionStorage;
        this.scheduleFormatter = scheduleFormatter;
        this.historyFormatter = historyFormatter;
        this.analyticsService = analyticsService;
        this.analyticsFormatter = analyticsFormatter;
        this.managerAuthService = managerAuthService;
        this.managerSessionStorage = managerSessionStorage;
    }

    public BotResponse handle(long userId, String text) {
        if (text.equals("/start")) {
            return BotResponse.withKeyboard(
                    """
                    Привет! Я бот для расчёта кредита.

                    Команды:
                    /calculate — рассчитать кредит
                    /history — история запросов

                    Для менеджеров:
                    /manager_login <логин> <пароль> — авторизация
                    /analytics — агрегированная аналитика
                    /filter <мин.сумма> <макс.сумма> [ANNUITY|DIFFERENTIATED] — фильтр заявок
                    """,
                    List.of(List.of(
                            new BotButton("🧮 Рассчитать кредит", CALLBACK_CALCULATE),
                            new BotButton("📋 История", CALLBACK_HISTORY)
                    ))
            );
        }

        if (text.equals("/calculate")) {
            return startCalculation(userId);
        }

        if (text.equals("/history")) {
            return showHistory(userId);
        }

        if (text.startsWith("/manager_login")) {
            return BotResponse.text(handleManagerLogin(userId, text));
        }

        if (text.equals("/manager_logout")) {
            managerSessionStorage.revoke(userId);
            return BotResponse.text("Вы вышли из режима менеджера.");
        }

        if (text.equals("/analytics")) {
            return BotResponse.text(handleAnalytics(userId));
        }

        if (text.startsWith("/filter")) {
            return BotResponse.text(handleFilter(userId, text));
        }

        UserSession session = sessionStorage.getSession(userId);

        return switch (session.getState()) {
            case WAITING_AMOUNT -> handleAmount(session, text);
            case WAITING_MONTHS -> handleMonths(session, text);
            case WAITING_RATE -> handleRate(session, text);
            case WAITING_PAYMENT_TYPE -> handlePaymentType(userId, session, text);
            case NONE -> BotResponse.text("Неизвестная команда. Используйте /calculate или /history.");
        };
    }

    /**
     * Обрабатывает нажатия на inline-кнопки (callback-данные из Telegram).
     */
    public BotResponse handleCallback(long userId, String callbackData) {
        if (callbackData.equals(CALLBACK_CALCULATE)) {
            return startCalculation(userId);
        }

        if (callbackData.equals(CALLBACK_HISTORY)) {
            return showHistory(userId);
        }

        if (callbackData.startsWith(CALLBACK_PAYMENT_TYPE_PREFIX)) {
            return handlePaymentTypeCallback(userId, callbackData);
        }

        return BotResponse.text("Неизвестное действие.");
    }

    private BotResponse startCalculation(long userId) {
        UserSession session = sessionStorage.getSession(userId);
        session.clear();
        session.setState(UserState.WAITING_AMOUNT);
        return BotResponse.text("Введите сумму кредита:");
    }

    private BotResponse showHistory(long userId) {
        return BotResponse.text(historyFormatter.format(loanService.getUserHistory(userId)));
    }

    private BotResponse handleAmount(UserSession session, String text) {
        try {
            BigDecimal amount = new BigDecimal(text);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return BotResponse.text("Сумма должна быть больше 0. Введите сумму кредита:");
            }

            session.setAmount(amount);
            session.setState(UserState.WAITING_MONTHS);

            return BotResponse.text("Введите срок кредита в месяцах:");
        } catch (NumberFormatException exception) {
            return BotResponse.text("Некорректная сумма. Пример: 100000");
        }
    }

    private BotResponse handleMonths(UserSession session, String text) {
        try {
            int months = Integer.parseInt(text);

            if (months <= 0) {
                return BotResponse.text("Срок должен быть больше 0. Введите срок в месяцах:");
            }

            session.setMonths(months);
            session.setState(UserState.WAITING_RATE);

            return BotResponse.text("Введите годовую процентную ставку:");
        } catch (NumberFormatException exception) {
            return BotResponse.text("Некорректный срок. Пример: 12");
        }
    }

    private BotResponse handleRate(UserSession session, String text) {
        try {
            BigDecimal rate = new BigDecimal(text);

            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                return BotResponse.text("Ставка не может быть отрицательной. Введите ставку:");
            }

            session.setAnnualRate(rate);
            session.setState(UserState.WAITING_PAYMENT_TYPE);

            return BotResponse.withKeyboard(
                    "Выберите тип платежа:",
                    List.of(List.of(
                            new BotButton("Аннуитетный", CALLBACK_PAYMENT_TYPE_PREFIX + "ANNUITY"),
                            new BotButton("Дифференцированный", CALLBACK_PAYMENT_TYPE_PREFIX + "DIFFERENTIATED")
                    ))
            );
        } catch (NumberFormatException exception) {
            return BotResponse.text("Некорректная ставка. Пример: 12.5");
        }
    }

    private BotResponse handlePaymentType(long userId, UserSession session, String text) {
        PaymentType paymentType;

        if (text.equals("1")) {
            paymentType = PaymentType.ANNUITY;
        } else if (text.equals("2")) {
            paymentType = PaymentType.DIFFERENTIATED;
        } else {
            return BotResponse.text("Некорректный тип платежа. Введите 1 или 2, либо воспользуйтесь кнопками выше.");
        }

        return calculateAndRespond(userId, session, paymentType);
    }

    private BotResponse handlePaymentTypeCallback(long userId, String callbackData) {
        UserSession session = sessionStorage.getSession(userId);

        if (session.getState() != UserState.WAITING_PAYMENT_TYPE) {
            return BotResponse.text("Сессия расчёта истекла или уже завершена. Начните заново: /calculate");
        }

        PaymentType paymentType = callbackData.equals(CALLBACK_PAYMENT_TYPE_PREFIX + "ANNUITY")
                ? PaymentType.ANNUITY
                : PaymentType.DIFFERENTIATED;

        return calculateAndRespond(userId, session, paymentType);
    }

    private BotResponse calculateAndRespond(long userId, UserSession session, PaymentType paymentType) {
        LoanRequest request = new LoanRequest(
                userId,
                session.getAmount(),
                session.getMonths(),
                session.getAnnualRate(),
                paymentType
        );

        List<PaymentScheduleItem> schedule = loanService.calculateSchedule(request);

        session.clear();

        return BotResponse.text(scheduleFormatter.format(schedule));
    }

    private String handleManagerLogin(long userId, String text) {
        String[] parts = text.trim().split("\\s+");

        if (parts.length != 3) {
            return "Использование: /manager_login <логин> <пароль>";
        }

        String login = parts[1];
        String password = parts[2];

        if (managerAuthService.authenticate(login, password)) {
            managerSessionStorage.authorize(userId);
            return "Авторизация успешна. Доступны команды /analytics и /filter.";
        }

        return "Неверный логин или пароль.";
    }

    private String handleAnalytics(long userId) {
        if (!managerSessionStorage.isAuthorized(userId)) {
            return MANAGER_ACCESS_DENIED;
        }

        return analyticsFormatter.format(
                analyticsService.countAllRequests(),
                analyticsService.countByPaymentType(),
                analyticsService.countByLoanTerm()
        );
    }

    private String handleFilter(long userId, String text) {
        if (!managerSessionStorage.isAuthorized(userId)) {
            return MANAGER_ACCESS_DENIED;
        }

        String[] parts = text.trim().split("\\s+");

        if (parts.length < 3) {
            return "Использование: /filter <мин.сумма> <макс.сумма> [ANNUITY|DIFFERENTIATED]";
        }

        BigDecimal minAmount;
        BigDecimal maxAmount;

        try {
            minAmount = new BigDecimal(parts[1]);
            maxAmount = new BigDecimal(parts[2]);
        } catch (NumberFormatException exception) {
            return "Суммы должны быть числами. Пример: /filter 50000 500000";
        }

        PaymentType paymentType = null;

        if (parts.length >= 4) {
            try {
                paymentType = PaymentType.valueOf(parts[3].toUpperCase());
            } catch (IllegalArgumentException exception) {
                return "Неизвестный тип платежа. Используйте ANNUITY или DIFFERENTIATED.";
            }
        }

        List<LoanRequest> filtered = analyticsService.filter(minAmount, maxAmount, paymentType);

        return historyFormatter.format(
                filtered,
                "Результаты фильтрации:\n\n",
                "Нет заявок, соответствующих фильтру."
        );
    }
}
