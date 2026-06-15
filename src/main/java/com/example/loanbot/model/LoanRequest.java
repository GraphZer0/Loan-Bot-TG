package com.example.loanbot.model;

import java.math.BigDecimal;

public class LoanRequest {
    private final long userId;
    private final BigDecimal amount;
    private final int months;
    private final BigDecimal annualRate;
    private final PaymentType paymentType;

    public LoanRequest(long userId, BigDecimal amount, int months,
                       BigDecimal annualRate, PaymentType paymentType) {
        this.userId = userId;
        this.amount = amount;
        this.months = months;
        this.annualRate = annualRate;
        this.paymentType = paymentType;
    }

    public long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getMonths() {
        return months;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
}