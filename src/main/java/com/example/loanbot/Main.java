package com.example.loanbot;

import com.example.loanbot.bot.BotMessageHandler;
import com.example.loanbot.bot.LoanTelegramBot;
import com.example.loanbot.bot.UserSessionStorage;
import com.example.loanbot.calculator.PaymentCalculatorFactory;
import com.example.loanbot.formatter.HistoryFormatter;
import com.example.loanbot.formatter.PaymentScheduleFormatter;
import com.example.loanbot.repository.InMemoryLoanRequestRepository;
import com.example.loanbot.repository.LoanRequestRepository;
import com.example.loanbot.service.LoanService;
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

        LoanRequestRepository repository = new InMemoryLoanRequestRepository();

        LoanService loanService = new LoanService(
                new PaymentCalculatorFactory(),
                repository
        );

        BotMessageHandler messageHandler = new BotMessageHandler(
                loanService,
                new UserSessionStorage(),
                new PaymentScheduleFormatter(),
                new HistoryFormatter()
        );

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        LoanTelegramBot bot = new LoanTelegramBot(
                botToken,
                botUsername,
                messageHandler
        );

        botsApi.registerBot(bot);

        System.out.println("Бот запущен");
    }
}