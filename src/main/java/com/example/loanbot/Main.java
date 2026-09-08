package com.example.loanbot;

import com.example.loanbot.bot.BotMessageHandler;
import com.example.loanbot.bot.LoanTelegramBot;
import com.example.loanbot.bot.ManagerSessionStorage;
import com.example.loanbot.bot.UserSessionStorage;
import com.example.loanbot.calculator.PaymentCalculatorFactory;
import com.example.loanbot.formatter.AnalyticsFormatter;
import com.example.loanbot.formatter.HistoryFormatter;
import com.example.loanbot.formatter.PaymentScheduleFormatter;
import com.example.loanbot.repository.InMemoryLoanRequestRepository;
import com.example.loanbot.repository.LoanRequestRepository;
import com.example.loanbot.repository.SqliteLoanRequestRepository;
import com.example.loanbot.service.AnalyticsService;
import com.example.loanbot.service.LoanService;
import com.example.loanbot.service.ManagerAuthService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) throws Exception {
        String botToken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_USERNAME");

        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException("Переменная окружения BOT_TOKEN не задана");
        }

        if (botUsername == null || botUsername.isBlank()) {
            throw new IllegalStateException("Переменная окружения BOT_USERNAME не задана");
        }

        DefaultBotOptions botOptions = new DefaultBotOptions();
        String proxyHost = System.getenv("PROXY_HOST");
        String proxyPortEnv = System.getenv("PROXY_PORT");
        if (proxyHost != null && !proxyHost.isBlank() && proxyPortEnv != null && !proxyPortEnv.isBlank()) {
            botOptions.setProxyHost(proxyHost);
            botOptions.setProxyPort(Integer.parseInt(proxyPortEnv));
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            System.out.println("Используется SOCKS5-прокси: " + proxyHost + ":" + proxyPortEnv);
        }

        String managerLogin = System.getenv("MANAGER_LOGIN");
        String managerPassword = System.getenv("MANAGER_PASSWORD");

        String databasePath = System.getenv("DB_PATH");
        LoanRequestRepository repository;
        if (databasePath != null && !databasePath.isBlank()) {
            repository = new SqliteLoanRequestRepository(databasePath);
            System.out.println("Хранилище заявок: SQLite (" + databasePath + ")");
        } else {
            repository = new InMemoryLoanRequestRepository();
            System.out.println("Хранилище заявок: в памяти (DB_PATH не задан)");
        }

        LoanService loanService = new LoanService(
                new PaymentCalculatorFactory(),
                repository
        );

        AnalyticsService analyticsService = new AnalyticsService(repository);

        BotMessageHandler messageHandler = new BotMessageHandler(
                loanService,
                new UserSessionStorage(),
                new PaymentScheduleFormatter(),
                new HistoryFormatter(),
                analyticsService,
                new AnalyticsFormatter(),
                new ManagerAuthService(managerLogin, managerPassword),
                new ManagerSessionStorage()
        );

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        LoanTelegramBot bot = new LoanTelegramBot(
                botOptions,
                botToken,
                botUsername,
                messageHandler
        );

        botsApi.registerBot(bot);

        System.out.println("Бот запущен");
    }
}
