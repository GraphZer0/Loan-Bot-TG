package com.example.loanbot.calculator;

import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentScheduleItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AnnuityPaymentCalculator implements PaymentCalculator {

    private static final int SCALE = 2;

    @Override
    public List<PaymentScheduleItem> calculate(LoanRequest request) {
        BigDecimal amount = request.getAmount();
        int months = request.getMonths();

        BigDecimal monthlyRate = request.getAnnualRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, monthlyRate, months);

        List<PaymentScheduleItem> schedule = new ArrayList<>();

        BigDecimal remainingDebt = amount;

        for (int month = 1; month <= months; month++) {
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal principalPayment = monthlyPayment
                    .subtract(interestPayment)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            remainingDebt = remainingDebt
                    .subtract(principalPayment)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            if (month == months) {
                principalPayment = principalPayment.add(remainingDebt);
                monthlyPayment = principalPayment.add(interestPayment);
                remainingDebt = BigDecimal.ZERO;
            }

            schedule.add(new PaymentScheduleItem(
                    month,
                    monthlyPayment,
                    principalPayment,
                    interestPayment,
                    remainingDebt
            ));
        }

        return schedule;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal monthlyRate, int months) {
        double rate = monthlyRate.doubleValue();

        double coefficient = rate * Math.pow(1 + rate, months)
                / (Math.pow(1 + rate, months) - 1);

        return amount
                .multiply(BigDecimal.valueOf(coefficient))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}