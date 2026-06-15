package com.example.loanbot.formatter;

import com.example.loanbot.model.LoanRequest;

import java.util.List;

public class HistoryFormatter {

    public String format(List<LoanRequest> history) {
        if (history.isEmpty()) {
            return "История запросов пока пустая.";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("История ваших запросов:\n\n");

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