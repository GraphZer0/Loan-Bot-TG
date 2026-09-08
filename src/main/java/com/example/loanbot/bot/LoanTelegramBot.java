package com.example.loanbot.bot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class LoanTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final BotMessageHandler messageHandler;

    public LoanTelegramBot(String botToken,
                           String botUsername,
                           BotMessageHandler messageHandler) {
        super(botToken);
        this.botUsername = botUsername;
        this.messageHandler = messageHandler;
    }

    public LoanTelegramBot(DefaultBotOptions options,
                           String botToken,
                           String botUsername,
                           BotMessageHandler messageHandler) {
        super(options, botToken);
        this.botUsername = botUsername;
        this.messageHandler = messageHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();

        BotResponse response = messageHandler.handle(userId, text);

        sendResponse(chatId, response);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        String callbackData = callbackQuery.getData();

        answerCallback(callbackQuery.getId());

        BotResponse response = messageHandler.handleCallback(userId, callbackData);

        sendResponse(chatId, response);
    }

    private void answerCallback(String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);

        try {
            execute(answer);
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
    }

    private void sendResponse(long chatId, BotResponse response) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response.getText());

        if (response.hasKeyboard()) {
            message.setReplyMarkup(buildKeyboard(response.getKeyboard()));
        }

        try {
            execute(message);
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
    }

    private InlineKeyboardMarkup buildKeyboard(List<List<BotButton>> buttons) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (List<BotButton> row : buttons) {
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();

            for (BotButton button : row) {
                InlineKeyboardButton inlineButton = new InlineKeyboardButton();
                inlineButton.setText(button.label());
                inlineButton.setCallbackData(button.callbackData());
                buttonRow.add(inlineButton);
            }

            rows.add(buttonRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}
