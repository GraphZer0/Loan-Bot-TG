package com.example.loanbot.bot;

import java.util.List;

/**
 * Ответ обработчика сообщений: текст плюс необязательная inline-клавиатура.
 * Позволяет BotMessageHandler оставаться независимым от классов Telegram API
 * (LoanTelegramBot сам решает, как превратить BotResponse в SendMessage).
 */
public class BotResponse {

    private final String text;
    private final List<List<BotButton>> keyboard;

    private BotResponse(String text, List<List<BotButton>> keyboard) {
        this.text = text;
        this.keyboard = keyboard;
    }

    public static BotResponse text(String text) {
        return new BotResponse(text, List.of());
    }

    public static BotResponse withKeyboard(String text, List<List<BotButton>> keyboard) {
        return new BotResponse(text, keyboard);
    }

    public String getText() {
        return text;
    }

    public List<List<BotButton>> getKeyboard() {
        return keyboard;
    }

    public boolean hasKeyboard() {
        return keyboard != null && !keyboard.isEmpty();
    }
}
