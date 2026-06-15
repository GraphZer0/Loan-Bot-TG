package com.example.loanbot.service;

import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentType;
import com.example.loanbot.repository.LoanRequestRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsService {

    private final LoanRequestRepository loanRequestRepository;

    public AnalyticsService(LoanRequestRepository loanRequestRepository) {
        this.loanRequestRepository = loanRequestRepository;
    }

    public long countAllRequests() {
        return loanRequestRepository.findAll().size();
    }

    public Map<PaymentType, Long> countByPaymentType() {
        return loanRequestRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        LoanRequest::getPaymentType,
                        Collectors.counting()
                ));
    }

    public List<LoanRequest> filterByAmount(BigDecimal minAmount, BigDecimal maxAmount) {
        return loanRequestRepository.findAll()
                .stream()
                .filter(request -> request.getAmount().compareTo(minAmount) >= 0)
                .filter(request -> request.getAmount().compareTo(maxAmount) <= 0)
                .toList();
    }

    public Map<Integer, Long> countByLoanTerm() {
        return loanRequestRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        LoanRequest::getMonths,
                        Collectors.counting()
                ));
    }
}