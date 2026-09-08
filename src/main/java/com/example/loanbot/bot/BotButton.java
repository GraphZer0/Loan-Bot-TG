package com.example.loanbot.bot;

/**
 * Кнопка inline-клавиатуры, независимая от конкретного API Telegram.
 * LoanTelegramBot преобразует её в InlineKeyboardButton при отправке.
 */
public record BotButton(String label, String callbackData) {
}
