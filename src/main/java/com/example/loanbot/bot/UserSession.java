package com.example.loanbot.bot;

import com.example.loanbot.model.PaymentType;

import java.math.BigDecimal;

public class UserSession {

    private UserState state = UserState.NONE;

    private BigDecimal amount;
    private Integer months;
    private BigDecimal annualRate;
    private PaymentType paymentType;

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void clear() {
        state = UserState.NONE;
        amount = null;
        months = null;
        annualRate = null;
        paymentType = null;
    }
}