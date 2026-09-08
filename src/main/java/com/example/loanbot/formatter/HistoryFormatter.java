package com.example.loanbot.formatter;

import com.example.loanbot.model.LoanRequest;

import java.util.List;

public class HistoryFormatter {

    public String format(List<LoanRequest> history) {
        return format(history, "История ваших запросов:\n\n", "История запросов пока пустая.");
    }

    /**
     * Форматирует произвольный список заявок (используется и для /history,
     * и для менеджерской команды /filter) с настраиваемым заголовком и
     * сообщением на случай пустого списка.
     */
    public String format(List<LoanRequest> history, String title, String emptyMessage) {
        if (history.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(title);

        for (int i = 0; i < history.size(); i++) {
            LoanRequest request = history.get(i);

            builder.append(i + 1)
                    .append(". Сумма: ")
                    .append(request.getAmount())
                    .append("\n")
                    .append("Срок: ")
                    .append(request.getMonths())
                    .append(" мес.\n")
                    .append("Ставка: ")
                    .append(request.getAnnualRate())
                    .append("%\n")
                    .append("Тип платежа: ")
                    .append(request.getPaymentType())
                    .append("\n\n");
        }

        return builder.toString();
    }
}
