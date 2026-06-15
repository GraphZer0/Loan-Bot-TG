package com.example.loanbot.calculator;

import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentScheduleItem;

import java.util.List;

public interface PaymentCalculator {
    List<PaymentScheduleItem> calculate(LoanRequest request);
}