package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.transaction.response.StatisticsResponse;
import com.bank.transaction_service.dto.transaction.response.TransactionResponse;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.service.TransactionService;
import com.bank.transaction_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTransactionController {

    private final TransactionService transactionService;

    // =========================
    // 📜 VIEW
    // =========================

    @GetMapping
    public Page<TransactionResponse> getAll(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getAllTransactions(
                accountNumber, status, page, size
        );
    }

    @GetMapping("/{id}")
    public TransactionResponse getDetail(@PathVariable Long id) {
        return transactionService.getTransactionDetail(id);
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

    // =========================
    // 🔥 ACTION
    // =========================

    @PostMapping("/{id}/retry")
    public void retry(@PathVariable Long id) {
        Long adminId = SecurityUtils.getUserId();
        transactionService.retryTransaction(id, adminId);
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        Long adminId = SecurityUtils.getUserId();
        transactionService.cancelTransaction(id, adminId);
    }

    @PostMapping("/{id}/refund")
    public void refund(@PathVariable Long id) {
        Long adminId = SecurityUtils.getUserId();
        transactionService.refundTransaction(id, adminId);
    }
}