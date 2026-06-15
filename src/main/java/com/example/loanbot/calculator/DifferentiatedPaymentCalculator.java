package com.example.loanbot.calculator;

import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentScheduleItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DifferentiatedPaymentCalculator implements PaymentCalculator {

    private static final int SCALE = 2;

    @Override
    public List<PaymentScheduleItem> calculate(LoanRequest request) {
        BigDecimal amount = request.getAmount();
        int months = request.getMonths();

        BigDecimal monthlyRate = request.getAnnualRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal principalPayment = amount
                .divide(BigDecimal.valueOf(months), SCALE, RoundingMode.HALF_UP);

        List<PaymentScheduleItem> schedule = new ArrayList<>();

        BigDecimal remainingDebt = amount;

        for (int month = 1; month <= months; month++) {
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            if (month == months) {
                principalPayment = remainingDebt;
            }

            BigDecimal totalPayment = principalPayment
                    .add(interestPayment)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            remainingDebt = remainingDebt
                    .subtract(principalPayment)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            schedule.add(new PaymentScheduleItem(
                    month,
                    totalPayment,
                    principalPayment,
                    interestPayment,
                    remainingDebt
            ));
        }

        return schedule;
    }
}