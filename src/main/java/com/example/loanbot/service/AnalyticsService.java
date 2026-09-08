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

    /**
     * Единая точка фильтрации по сумме и/или типу платежа.
     * Любой из параметров может быть null — тогда соответствующее условие не применяется.
     */
    public List<LoanRequest> filter(BigDecimal minAmount, BigDecimal maxAmount, PaymentType paymentType) {
        return loanRequestRepository.findAll()
                .stream()
                .filter(request -> minAmount == null || request.getAmount().compareTo(minAmount) >= 0)
                .filter(request -> maxAmount == null || request.getAmount().compareTo(maxAmount) <= 0)
                .filter(request -> paymentType == null || request.getPaymentType() == paymentType)
                .toList();
    }

    public List<LoanRequest> filterByAmount(BigDecimal minAmount, BigDecimal maxAmount) {
        return filter(minAmount, maxAmount, null);
    }

    public List<LoanRequest> filterByPaymentType(PaymentType paymentType) {
        return filter(null, null, paymentType);
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
