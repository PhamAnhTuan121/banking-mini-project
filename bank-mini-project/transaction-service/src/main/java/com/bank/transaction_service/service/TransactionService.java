package com.bank.transaction_service.service;

import com.bank.transaction_service.dto.transaction.request.ConfirmTransferRequest;
import com.bank.transaction_service.dto.transaction.request.TransactionRequest;
import com.bank.transaction_service.dto.transaction.response.StatisticsResponse;
import com.bank.transaction_service.dto.transaction.response.TransactionResponse;
import com.bank.transaction_service.dto.transaction.response.TransferResponseConfirm;
import com.bank.transaction_service.dto.transaction.response.TransferResponseRequest;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.entity.TransactionType;
import org.springframework.data.domain.Page;

public interface TransactionService {

    TransferResponseRequest requestTransfer(TransactionRequest request, Long userId);

    TransferResponseConfirm confirmTransfer(ConfirmTransferRequest request, Long userId);

    void cancelTransaction(Long id, Long adminId);

    void refundTransaction(Long id, Long adminId);

    Page<TransactionResponse> getHistory(
            Long userId,
            TransactionType type,
            TransactionStatus status,
            String fromDate,
            String toDate,
            int page,
            int size
    );

    Page<TransactionResponse> getInternalTransactions(
            String accountNumber,
            TransactionType type,
            TransactionStatus status,
            int page,
            int size
    );

    TransactionResponse getTransactionDetail(Long id);

    void retryTransaction(Long id, Long adminId);

    Page<TransactionResponse> getAllTransactions(
            String accountNumber,
            TransactionStatus status,
            int page,
            int size
    );

    void resendOtp(String identifier);

    StatisticsResponse getStatistics();

    Page<TransactionResponse> getFailedTransactions(int page, int size);
}
