package com.example.loanbot.calculator;

import com.example.loanbot.model.PaymentType;

public class PaymentCalculatorFactory {

    public PaymentCalculator create(PaymentType paymentType) {
        return switch (paymentType) {
            case ANNUITY -> new AnnuityPaymentCalculator();
            case DIFFERENTIATED -> new DifferentiatedPaymentCalculator();
        };
    }
}