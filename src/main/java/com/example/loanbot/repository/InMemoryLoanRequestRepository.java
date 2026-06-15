package com.example.loanbot.repository;

import com.example.loanbot.model.LoanRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryLoanRequestRepository implements LoanRequestRepository {

    private final Map<Long, List<LoanRequest>> requestsByUserId = new HashMap<>();

    @Override
    public void save(LoanRequest request) {
        requestsByUserId
                .computeIfAbsent(request.getUserId(), userId -> new ArrayList<>())
                .add(request);
    }

    @Override
    public List<LoanRequest> findByUserId(long userId) {
        return requestsByUserId.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public List<LoanRequest> findAll() {
        List<LoanRequest> allRequests = new ArrayList<>();

        for (List<LoanRequest> userRequests : requestsByUserId.values()) {
            allRequests.addAll(userRequests);
        }

        return allRequests;
    }
}