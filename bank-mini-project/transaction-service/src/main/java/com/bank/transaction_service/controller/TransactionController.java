package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.transaction.response.TransactionResponse;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.entity.TransactionType;
import com.bank.transaction_service.service.TransactionService;
import com.bank.transaction_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/transactions")
@PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;


//    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
//    @GetMapping("/history/{accountNumber}")
//    public ResponseEntity<List<?>> history(@PathVariable String accountNumber) {
//        return ResponseEntity.ok(transactionService.getHistory(accountNumber));
//    }

    @GetMapping
    public Page<TransactionResponse> getTransactions(
            @RequestParam String accountNumber,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return transactionService.getInternalTransactions(accountNumber, type, status, page, size);
    }

    @GetMapping("/{id}")
    public TransactionResponse getDetail(@PathVariable Long id) {
        return transactionService.getTransactionDetail(id);
    }

    @PostMapping("/{id}/retry")
    public void retry(@PathVariable Long id) {
        Long employeeId = SecurityUtils.getUserId();
        transactionService.retryTransaction(id, employeeId);
    }

}
