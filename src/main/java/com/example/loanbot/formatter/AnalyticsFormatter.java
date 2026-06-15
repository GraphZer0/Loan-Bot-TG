package com.example.loanbot.formatter;

import com.example.loanbot.model.PaymentType;

import java.util.Map;

public class AnalyticsFormatter {

    public String format(long totalRequests,
                         Map<PaymentType, Long> requestsByPaymentType,
                         Map<Integer, Long> requestsByLoanTerm) {

        StringBuilder builder = new StringBuilder();

        builder.append("Аналитика по заявкам:\n\n");

        builder.append("Всего заявок: ")
                .append(totalRequests)
                .append("\n\n");

        builder.append("По типам платежей:\n");

        for (Map.Entry<PaymentType, Long> entry : requestsByPaymentType.entrySet()) {
            builder.append("- ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        builder.append("\nПо срокам кредита:\n");

        for (Map.Entry<Integer, Long> entry : requestsByLoanTerm.entrySet()) {
            builder.append("- ")
                    .append(entry.getKey())
                    .append(" мес.: ")
                    .append(entry.getValue())
                    .append("\n");
        }

        return builder.toString();
    }
}