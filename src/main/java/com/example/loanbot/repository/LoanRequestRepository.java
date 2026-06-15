package com.example.loanbot.repository;

import com.example.loanbot.model.LoanRequest;

import java.util.List;

public interface LoanRequestRepository {

    void save(LoanRequest request);

    List<LoanRequest> findByUserId(long userId);

    List<LoanRequest> findAll();
}