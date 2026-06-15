package com.example.loanbot.formatter;

import com.example.loanbot.model.PaymentScheduleItem;

import java.util.List;

public class PaymentScheduleFormatter {

    public String format(List<PaymentScheduleItem> schedule) {
        StringBuilder builder = new StringBuilder();

        builder.append("График платежей:\n\n");

        for (PaymentScheduleItem item : schedule) {
            builder.append("Месяц: ")
                    .append(item.getMonth())
                    .append("\n")
                    .append("Платёж: ")
                    .append(item.getTotalPayment())
                    .append("\n")
                    .append("Основной долг: ")
                    .append(item.getPrincipalPayment())
                    .append("\n")
                    .append("Проценты: ")
                    .append(item.getInterestPayment())
                    .append("\n")
                    .append("Остаток долга: ")
                    .append(item.getRemainingDebt())
                    .append("\n\n");
        }

        return builder.toString();
    }
}