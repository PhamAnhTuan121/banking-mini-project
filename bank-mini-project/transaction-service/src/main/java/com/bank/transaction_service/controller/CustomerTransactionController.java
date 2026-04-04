package com.bank.transaction_service.controller;

import com.bank.transaction_service.dto.transaction.request.ConfirmTransferRequest;
import com.bank.transaction_service.dto.transaction.request.TransactionRequest;
import com.bank.transaction_service.dto.transaction.response.TransferResponseConfirm;
import com.bank.transaction_service.dto.transaction.response.TransferResponseRequest;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.entity.TransactionType;
import com.bank.transaction_service.service.TransactionService;
import com.bank.transaction_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerTransactionController {

    private final TransactionService transactionService;

    @PostMapping("/request")
    public TransferResponseRequest requestTransfer(
            @RequestBody TransactionRequest request
    ) {
        Long userId = SecurityUtils.getUserId();
        return transactionService.requestTransfer(request, userId);
    }

    @PostMapping("/confirm")
    public TransferResponseConfirm confirmTransfer(
            @RequestBody ConfirmTransferRequest request
    ) {
        Long userId = SecurityUtils.getUserId();
        return transactionService.confirmTransfer(request, userId);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtils.getUserId();
        return ResponseEntity.ok(
                transactionService.getHistory(
                        userId, type, status, fromDate, toDate, page, size
                )
        );
    }
}
