package com.example.loanbot.model;

import java.math.BigDecimal;

public class PaymentScheduleItem {
    private final int month;
    private final BigDecimal totalPayment;
    private final BigDecimal principalPayment;
    private final BigDecimal interestPayment;
    private final BigDecimal remainingDebt;

    public PaymentScheduleItem(int month,
                               BigDecimal totalPayment,
                               BigDecimal principalPayment,
                               BigDecimal interestPayment,
                               BigDecimal remainingDebt) {
        this.month = month;
        this.totalPayment = totalPayment;
        this.principalPayment = principalPayment;
        this.interestPayment = interestPayment;
        this.remainingDebt = remainingDebt;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public BigDecimal getPrincipalPayment() {
        return principalPayment;
    }

    public BigDecimal getInterestPayment() {
        return interestPayment;
    }

    public BigDecimal getRemainingDebt() {
        return remainingDebt;
    }
}