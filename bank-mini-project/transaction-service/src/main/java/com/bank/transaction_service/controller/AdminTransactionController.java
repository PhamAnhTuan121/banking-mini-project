package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.transaction.response.StatisticsResponse;
import com.bank.transaction_service.dto.transaction.response.TransactionResponse;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Page<TransactionResponse> getAll(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getInternalTransactions(
                accountNumber, null, status, page, size
        );
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return transactionService.getStatistics();
    }

    @GetMapping("/failed")
    public Page<TransactionResponse> failed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getFailedTransactions(page, size);
    }

}