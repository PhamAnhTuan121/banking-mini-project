package com.bank.transaction_service.service.impl;

import com.bank.bank_common.dto.account.request.DepositRequest;
import com.bank.bank_common.dto.account.request.WithdrawRequest;
import com.bank.bank_common.dto.account.response.AccountResponse;
import com.bank.bank_common.dto.event.TransactionSuccessEvent;
import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.dto.otp.request.ResendOtpRequest;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.transaction_service.client_wrapper.AccountService;
import com.bank.transaction_service.client_wrapper.OtpService;
import com.bank.transaction_service.constant.TransactionAuditEvent;
import com.bank.transaction_service.dto.transaction.request.ConfirmTransferRequest;
import com.bank.transaction_service.dto.transaction.request.TransactionRequest;
import com.bank.transaction_service.dto.transaction.response.*;
import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.entity.TransactionType;
import com.bank.transaction_service.kafka.NotificationProducer;
import com.bank.transaction_service.mapper.TransactionMapper;
import com.bank.transaction_service.repository.TransactionRepository;
import com.bank.transaction_service.service.TransactionService;
import com.bank.transaction_service.util.AuditHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationProducer notificationProducer;
    private final AccountService accountService;
    private final OtpService otpService;
    private final AuditHelper auditHelper;

    @Override
    public TransferResponseRequest requestTransfer(TransactionRequest request, Long userId) {
        String toAccount = request.getToAccount().trim();

        if (toAccount.endsWith(".")) {
            toAccount =
                    toAccount.substring(0, toAccount.length() - 1);
        }

        AccountResponse fromAccount =
                accountService.getAccountByUserId(userId);

        AccountResponse sender =
                accountService.getByAccountNumber(
                        fromAccount.getAccountNumber()
                );

        if (fromAccount.getAccountNumber().equals(toAccount)) {
            throw new BusinessException(ErrorCode.SAME_ACCOUNT_TRANSFER);
        }

        if (request.getAmount().compareTo(new BigDecimal("10000")) < 0) {
            throw new BusinessException(ErrorCode.MIN_TRANSFER_AMOUNT);
        }

        try {
            accountService.getByAccountNumber(toAccount);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        String correlationId = UUID.randomUUID().toString();
        Transaction existing = transactionRepository
                .findByCorrelationId(correlationId)
                .orElse(null);
        if (existing != null) {
            return TransferResponseRequest.builder()
                    .correlationId(existing.getCorrelationId())
                    .status(existing.getStatus().name())
                    .message("Request already processed")
                    .build();
        }
        try {

            if (toAccount.endsWith(".")) {
                toAccount = toAccount.substring(0, toAccount.length() - 1);
            }

            if (fromAccount.getStatus().equals("FROZEN")) {
                throw new BusinessException(ErrorCode.ACCOUNT_FROZEN);
            }


            if (!sender.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED);
            }

            if (sender.getBalance().compareTo(request.getAmount()) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
            }

            Transaction tx = Transaction.builder()
                    .transactionType(TransactionType.TRANSFER)
                    .fromAccount(sender.getAccountNumber())
                    .toAccount(toAccount)
                    .amount(request.getAmount())
                    .correlationId(correlationId)
                    .status(TransactionStatus.PENDING)
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .description(request.getDescription())
                    .build();

            transactionRepository.save(tx);

            otpService.sendOtp(tx.getCorrelationId(), OtpType.TRANSFER);

            auditHelper.success(
                    userId,
                    TransactionAuditEvent.TRANSFER_REQUEST,
                    "TRANSFER_REQUEST",
                    "OTP sent",
                    Map.of(
                            "correlationId", correlationId,
                            "fromAccount", sender.getAccountNumber(),
                            "toAccount", toAccount,
                            "amount", request.getAmount()
                    )
            );

            return TransferResponseRequest.builder()
                    .correlationId(correlationId)
                    .status("OTP_SENT")
                    .message("OTP has been sent to your phone")
                    .build();

        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error during transfer request", ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public TransferResponseConfirm confirmTransfer(
            ConfirmTransferRequest request,
            Long userId) {

        Transaction tx = transactionRepository
                .findByCorrelationId(request.getIdentifier())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_TRANSACTION_STATE);
        }

        AccountResponse sender = accountService.getAccountByUserId(userId);

        if (!sender.getAccountNumber().equals(tx.getFromAccount())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TRANSFER);
        }

        if (sender.getBalance().compareTo(tx.getAmount()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        otpService.verifyOtp(
                request.getIdentifier(),
                request.getOtp(),
                OtpType.TRANSFER
        );

        try {
            accountService.withdraw(
                    tx.getFromAccount(),
                    new WithdrawRequest(tx.getAmount())
            );

            try {
                accountService.deposit(
                        tx.getToAccount(),
                        new DepositRequest(tx.getAmount())
                );

            } catch (Exception depositEx) {

                accountService.deposit(
                        tx.getFromAccount(),
                        new DepositRequest(tx.getAmount())
                );

                throw depositEx;
            }

            // ===== SUCCESS =====
            AccountResponse receiver =
                    accountService.getByAccountNumber(tx.getToAccount());

            notificationProducer.sendNotification(
                    TransactionSuccessEvent.builder()

                            .correlationId(tx.getCorrelationId())

                            .fromAccount(tx.getFromAccount())

                            .toAccount(tx.getToAccount())

                            .amount(tx.getAmount())

                            .description(tx.getDescription())

                            .bankName("NICE BANK BAC GIANG")

                            .transactionTime(LocalDateTime.now())

                            .senderUserId(sender.getUserId())

                            .receiverUserId(receiver.getUserId())

                            .build()
            );

            tx.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(tx);
            auditHelper.success(
                    userId,
                    TransactionAuditEvent.TRANSFER_CONFIRM,
                    "TRANSFER_CONFIRM",
                    "Transfer success",
                    Map.of("correlationId", request.getIdentifier())
            );

            return buildSuccessResponse(tx);

        } catch (BusinessException ex) {

            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);

            auditHelper.fail(
                    userId,
                    TransactionAuditEvent.TRANSFER_CONFIRM,
                    "TRANSFER_CONFIRM",
                    ex.getErrorCode().getMessage(),
                    Map.of("correlationId", request.getIdentifier())
            );

            throw ex;

        } catch (Exception ex) {

            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);

            auditHelper.fail(
                    userId,
                    TransactionAuditEvent.TRANSFER_CONFIRM,
                    "TRANSFER_CONFIRM",
                    "System error",
                    Map.of("correlationId", request.getIdentifier())
            );

            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private TransferResponseConfirm handleSystemFailure(
            Transaction tx,
            Long userId,
            Exception ex) {

        tx.setStatus(TransactionStatus.FAILED);
        transactionRepository.save(tx);

        auditHelper.fail(
                userId,
                TransactionAuditEvent.TRANSFER_CONFIRM,
                "TRANSFER_CONFIRM",
                "System error during transfer",
                Map.of(
                        "correlationId", tx.getCorrelationId(),
                        "error", ex.getMessage()
                )
        );

        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }

    private TransferResponseConfirm buildSuccessResponse(Transaction tx) {
        return TransferResponseConfirm.builder()
                .correlationId(tx.getCorrelationId())
                .transactionType(tx.getTransactionType().name())
                .fromAccount(tx.getFromAccount())
                .toAccount(tx.getToAccount())
                .amount(tx.getAmount())
                .status(tx.getStatus())
                .description(tx.getDescription())
                .message("Transfer completed successfully")
                .build();
    }

    private TransferResponseConfirm handleBusinessFailure(
            Transaction tx,
            Long userId,
            BusinessException ex) {

        tx.setStatus(TransactionStatus.FAILED);
        transactionRepository.save(tx);

        auditHelper.fail(
                userId,
                TransactionAuditEvent.TRANSFER_CONFIRM,
                "TRANSFER_CONFIRM",
                "Business failure: " + ex.getErrorCode().getMessage(),
                Map.of("correlationId", tx.getCorrelationId())
        );

        throw ex;
    }

    // =============================================
    // 📜 GET HISTORY
    // =============================================
    @Override
    public Page<TransactionResponse> getHistory(
            Long userId,
            String direction,
            String fromDate,
            String toDate,
            int page,
            int size
    ) {

        AccountResponse account = accountService.getAccountByUserId(userId);
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        LocalDateTime from = (fromDate != null && !fromDate.isBlank())
                ? LocalDateTime.parse(fromDate)
                : null;

        LocalDateTime to = (toDate != null && !toDate.isBlank())
                ? LocalDateTime.parse(toDate)
                : null;

        Page<Transaction> result;

        if ("IN".equalsIgnoreCase(direction)) {

            result = transactionRepository.findIncoming(
                    accountNumber, from, to, pageable
            );

        } else if ("OUT".equalsIgnoreCase(direction)) {

            result = transactionRepository.findOutgoing(
                    accountNumber, from, to, pageable
            );

        } else {

            result = transactionRepository.findAllHistory(
                    accountNumber, from, to, pageable
            );
        }

        return result.map(tx -> TransactionResponse.builder()
                .id(tx.getId())
                .fromAccount(tx.getFromAccount())
                .toAccount(tx.getToAccount())
                .amount(tx.getAmount())
                .timestamp(tx.getCreatedAt())
                .direction(accountNumber.equals(tx.getFromAccount()) ? "OUT" : "IN")
                .description(tx.getDescription())
                .build()
        );
    }

    @Override
    public Page<TransactionResponse> getInternalTransactions(
            String accountNumber,
            TransactionType type,
            TransactionStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Transaction> transactions;

        if (status != null && type != null) {
            transactions = transactionRepository.findByAccountAndTypeAndStatus(
                    accountNumber,
                    type,
                    status,
                    pageable
            );
        } else if (status != null) {
            transactions = transactionRepository.findByAccountAndStatus(
                    accountNumber,
                    status,
                    pageable
            );
        } else {
            transactions = transactionRepository.findByAccount(accountNumber, pageable);
        }

        return transactions.map(transactionMapper::toResponse);
    }

    @Override
    public TransactionResponse getTransactionDetail(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));
        return transactionMapper.toResponse(tx);
    }

    @Override
    @Transactional
    public void retryTransaction(Long id, Long adminId) {

        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (tx.getStatus() != TransactionStatus.FAILED) {
            throw new BusinessException(ErrorCode.INVALID_TRANSACTION_STATE);
        }

        try {
            AccountResponse sender =
                    accountService.getByAccountNumber(tx.getFromAccount());

            if (sender.getBalance().compareTo(tx.getAmount()) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
            }
            accountService.withdraw(
                    tx.getFromAccount(),
                    new WithdrawRequest(tx.getAmount())
            );

            try {
                accountService.deposit(
                        tx.getToAccount(),
                        new DepositRequest(tx.getAmount())
                );

            } catch (Exception depositEx) {
                accountService.deposit(
                        tx.getFromAccount(),
                        new DepositRequest(tx.getAmount())
                );

                throw depositEx;
            }
            tx.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(tx);
            auditHelper.success(
                    adminId,
                    TransactionAuditEvent.RETRY_TRANSACTION,
                    "RETRY_TRANSACTION",
                    "Retry success",
                    Map.of("transactionId", id)
            );

        } catch (BusinessException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);
            auditHelper.fail(
                    adminId,
                    TransactionAuditEvent.RETRY_TRANSACTION,
                    "RETRY_TRANSACTION",
                    ex.getErrorCode().getMessage(),
                    Map.of("transactionId", id)
            );

            throw ex;
        } catch (Exception ex) {

            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);

            auditHelper.fail(
                    adminId,
                    TransactionAuditEvent.RETRY_TRANSACTION,
                    "RETRY_TRANSACTION",
                    "System error",
                    Map.of("transactionId", id)
            );

            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingTransactions() {

        List<Transaction> txs =
                transactionRepository.findByStatusAndExpiredAtBefore(
                        TransactionStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Transaction tx : txs) {
            tx.setStatus(TransactionStatus.EXPIRED);
            transactionRepository.save(tx);
            auditHelper.fail(
                    null,
                    TransactionAuditEvent.TRANSFER_EXPIRED,
                    "TRANSFER_EXPIRED",
                    "Transaction expired (OTP timeout)",
                    Map.of("correlationId", tx.getCorrelationId())
            );
        }
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(
            String accountNumber,
            TransactionStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Transaction> transactions;
        if (accountNumber != null
                && !accountNumber.isBlank()
                && status != null) {

            transactions =
                    transactionRepository
                            .findByAccountAndStatus(
                                    accountNumber,
                                    status,
                                    pageable
                            );

        } else if (accountNumber != null
                && !accountNumber.isBlank()) {

            transactions =
                    transactionRepository
                            .findByAccount(
                                    accountNumber,
                                    pageable
                            );
        } else if (status != null) {

            transactions =
                    transactionRepository
                            .findByStatus(
                                    status,
                                    pageable
                            );
        } else {

            transactions =
                    transactionRepository
                            .findAll(pageable);
        }

        return transactions.map(
                transactionMapper::toResponse
        );
    }

    @Override
    public StatisticsResponse getStatistics() {

        long total = transactionRepository.count();

        long failed = transactionRepository.countByStatus(TransactionStatus.FAILED);

        long today = transactionRepository.countToday();

        Double totalAmount = transactionRepository.sumAmount();

        return StatisticsResponse.builder()
                .totalTransactions(total)
                .failedTransactions(failed)
                .todayTransactions(today)
                .totalAmount(totalAmount)
                .build();
    }

    @Override
    public Page<TransactionResponse> getFailedTransactions(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Transaction> txs = transactionRepository
                .findByStatus(TransactionStatus.FAILED, pageable);

        return txs.map(transactionMapper::toResponse);
    }

    @Override
    public void resendOtp(String correlationId) {

        correlationId = correlationId.trim().toLowerCase();

        ResendOtpRequest request = new ResendOtpRequest(
                correlationId,
                OtpType.TRANSFER
        );

        otpService.resendOtp(request.getIdentifier());
    }

    @Override
    @Transactional
    public void cancelTransaction(Long id, Long adminId) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            throw new BusinessException(ErrorCode.CANNOT_CANCEL_SUCCESS);
        }

        tx.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(tx);

        auditHelper.success(
                adminId,
                "CANCEL_TRANSACTION",
                "ADMIN_ACTION",
                "Transaction cancelled by admin",
                Map.of("transactionId", id)
        );
    }

    @Override
    @Transactional
    public void refundTransaction(Long id, Long adminId) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (tx.getStatus() == TransactionStatus.REFUNDED) {
            throw new BusinessException(ErrorCode.ALREADY_REFUNDED);
        }

        if (tx.getStatus() != TransactionStatus.SUCCESS) {
            throw new BusinessException(ErrorCode.INVALID_TRANSACTION_STATE);
        }

        accountService.withdraw(tx.getToAccount(), new WithdrawRequest(tx.getAmount()));
        accountService.deposit(tx.getFromAccount(), new DepositRequest(tx.getAmount()));

        tx.setStatus(TransactionStatus.REFUNDED);
        transactionRepository.save(tx);

        auditHelper.success(
                adminId,
                "REFUND_TRANSACTION",
                "ADMIN_ACTION",
                "Refund completed",
                Map.of("transactionId", id)
        );
    }

}
