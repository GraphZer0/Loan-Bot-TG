package com.example.loanbot.bot;

import com.example.loanbot.formatter.HistoryFormatter;
import com.example.loanbot.formatter.PaymentScheduleFormatter;
import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentScheduleItem;
import com.example.loanbot.model.PaymentType;
import com.example.loanbot.service.LoanService;

import java.math.BigDecimal;
import java.util.List;

public class BotMessageHandler {

    private final LoanService loanService;
    private final UserSessionStorage sessionStorage;
    private final PaymentScheduleFormatter scheduleFormatter;
    private final HistoryFormatter historyFormatter;

    public BotMessageHandler(LoanService loanService,
                             UserSessionStorage sessionStorage,
                             PaymentScheduleFormatter scheduleFormatter,
                             HistoryFormatter historyFormatter) {
        this.loanService = loanService;
        this.sessionStorage = sessionStorage;
        this.scheduleFormatter = scheduleFormatter;
        this.historyFormatter = historyFormatter;
    }

    public String handle(long userId, String text) {
        if (text.equals("/start")) {
            return """
                    Привет! Я бот для расчёта кредита.

                    Команды:
                    /calculate — рассчитать кредит
                    /history — история запросов
                    """;
        }

        if (text.equals("/calculate")) {
            UserSession session = sessionStorage.getSession(userId);
            session.clear();
            session.setState(UserState.WAITING_AMOUNT);
            return "Введите сумму кредита:";
        }

        if (text.equals("/history")) {
            return historyFormatter.format(loanService.getUserHistory(userId));
        }

        UserSession session = sessionStorage.getSession(userId);

        return switch (session.getState()) {
            case WAITING_AMOUNT -> handleAmount(session, text);
            case WAITING_MONTHS -> handleMonths(session, text);
            case WAITING_RATE -> handleRate(session, text);
            case WAITING_PAYMENT_TYPE -> handlePaymentType(userId, session, text);
            case NONE -> "Неизвестная команда. Используйте /calculate или /history.";
        };
    }

    private String handleAmount(UserSession session, String text) {
        try {
            BigDecimal amount = new BigDecimal(text);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return "Сумма должна быть больше 0. Введите сумму кредита:";
            }

            session.setAmount(amount);
            session.setState(UserState.WAITING_MONTHS);

            return "Введите срок кредита в месяцах:";
        } catch (NumberFormatException exception) {
            return "Некорректная сумма. Пример: 100000";
        }
    }

    private String handleMonths(UserSession session, String text) {
        try {
            int months = Integer.parseInt(text);

            if (months <= 0) {
                return "Срок должен быть больше 0. Введите срок в месяцах:";
            }

            session.setMonths(months);
            session.setState(UserState.WAITING_RATE);

            return "Введите годовую процентную ставку:";
        } catch (NumberFormatException exception) {
            return "Некорректный срок. Пример: 12";
        }
    }

    private String handleRate(UserSession session, String text) {
        try {
            BigDecimal rate = new BigDecimal(text);

            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                return "Ставка не может быть отрицательной. Введите ставку:";
            }

            session.setAnnualRate(rate);
            session.setState(UserState.WAITING_PAYMENT_TYPE);

            return """
                    Выберите тип платежа:
                    1 — аннуитетный
                    2 — дифференцированный
                    """;
        } catch (NumberFormatException exception) {
            return "Некорректная ставка. Пример: 12.5";
        }
    }

    private String handlePaymentType(long userId, UserSession session, String text) {
        PaymentType paymentType;

        if (text.equals("1")) {
            paymentType = PaymentType.ANNUITY;
        } else if (text.equals("2")) {
            paymentType = PaymentType.DIFFERENTIATED;
        } else {
            return "Некорректный тип платежа. Введите 1 или 2.";
        }

        LoanRequest request = new LoanRequest(
                userId,
                session.getAmount(),
                session.getMonths(),
                session.getAnnualRate(),
                paymentType
        );

        List<PaymentScheduleItem> schedule = loanService.calculateSchedule(request);

        session.clear();

        return scheduleFormatter.format(schedule);
    }
}