package com.example.loanbot.service;

import com.example.loanbot.calculator.PaymentCalculator;
import com.example.loanbot.calculator.PaymentCalculatorFactory;
import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentScheduleItem;
import com.example.loanbot.repository.LoanRequestRepository;

import java.util.List;

public class LoanService {

    private final PaymentCalculatorFactory calculatorFactory;
    private final LoanRequestRepository loanRequestRepository;

    public LoanService(PaymentCalculatorFactory calculatorFactory,
                       LoanRequestRepository loanRequestRepository) {
        this.calculatorFactory = calculatorFactory;
        this.loanRequestRepository = loanRequestRepository;
    }

    public List<PaymentScheduleItem> calculateSchedule(LoanRequest request) {
        PaymentCalculator calculator = calculatorFactory.create(request.getPaymentType());

        List<PaymentScheduleItem> schedule = calculator.calculate(request);

        loanRequestRepository.save(request);

        return schedule;
    }

    public List<LoanRequest> getUserHistory(long userId) {
        return loanRequestRepository.findByUserId(userId);
    }

    public List<LoanRequest> getAllRequests() {
        return loanRequestRepository.findAll();
    }
}