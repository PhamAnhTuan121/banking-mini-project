package com.bank.account_service.service.impl;

import com.bank.account_service.dto.request.AccountCreateRequest;

import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Status;
import com.bank.account_service.kafka.AuditLogAccountProducer;
import com.bank.account_service.mapper.AccountMapper;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import com.bank.bank_common.dto.account.response.AccountResponse;
import com.bank.bank_common.dto.audit_log.AuditEvent;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AuditLogAccountProducer auditProducer;

    @Override
    public AccountResponse getUserIdByUserId(Long userId) {
        Account account = accountRepository.findAccountByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        if (accountRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setStatus(Status.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setCurrencyCode("VND");
        account.setAccountType(Account.AccountType.CURRENT);
        account.setBalance(BigDecimal.ZERO);
        String accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            long randomPart = ThreadLocalRandom.current()
                    .nextLong(100_000_000L, 1_000_000_000L);
            accountNumber = "6886" + randomPart;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toResponse(account);
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return account.getBalance();
    }

    @Override
    @Transactional
    public AccountResponse deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);
        Account account = findActiveAccount(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse withdraw(String accountNumber, BigDecimal amount) {

        validateAmount(amount);

        Account account = findActiveAccount(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        return accountMapper.toResponse(account);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_AMOUNT);
        }
    }

    private Account findActiveAccount(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        switch (account.getStatus()) {
            case BLOCKED -> throw new BusinessException(ErrorCode.ACCOUNT_BLOCKED);
            case FROZEN -> throw new BusinessException(ErrorCode.ACCOUNT_FROZEN);
            case CLOSED -> throw new BusinessException(ErrorCode.ACCOUNT_CLOSED);
        }

        return account;
    }

    @Override
    @Transactional
    public void blockAccount(Long userId) {

        Account account = findAccount(userId);

        validateNotClosed(account);

        if (account.getStatus() == Status.BLOCKED) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_BLOCKED);
        }

        account.setStatus(Status.BLOCKED);
        accountRepository.save(account);

        sendAuditLog(
                userId,
                "BLOCK_ACCOUNT",
                "Block account " + account.getAccountNumber(),
                Map.of("status", "BLOCKED")
        );
    }

    @Override
    @Transactional
    public void unblockAccount(Long userId) {

        Account account = accountRepository
                .findAccountByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getStatus() == Status.ACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }

        if (account.getStatus() == Status.CLOSED) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_CLOSED);
        }

        account.setStatus(Status.ACTIVE);

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void closeAccount(Long userId) {

        Account account = findAccount(userId);

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_BALANCE_NOT_ZERO);
        }

        validateNotClosed(account);

        account.setStatus(Status.CLOSED);
        accountRepository.save(account);

        sendAuditLog(
                userId,
                "CLOSE_ACCOUNT",
                "Close account " + account.getAccountNumber(),
                Map.of("status", "CLOSED")
        );
    }

    @Override
    @Transactional
    public void freezeAccount(Long userId) {

        Account account = findAccount(userId);

        validateNotClosed(account);

        if (account.getStatus() == Status.FROZEN) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_FROZEN);
        }

        account.setStatus(Status.FROZEN);
        accountRepository.save(account);

        sendAuditLog(
                userId,
                "FREEZE_ACCOUNT",
                "Freeze account " + account.getAccountNumber(),
                Map.of("status", "FROZEN")
        );
    }

    @Override
    @Transactional
    public void unfreezeAccount(Long userId) {

        Account account = findAccount(userId);

        validateNotClosed(account);

        if (account.getStatus() != Status.FROZEN) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FROZEN);
        }

        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);

        sendAuditLog(
                userId,
                "UNFREEZE_ACCOUNT",
                "Unfreeze account " + account.getAccountNumber(),
                Map.of("status", "ACTIVE")
        );
    }

    private void validateNotClosed(Account account) {
        if (account.getStatus() == Status.CLOSED) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_CLOSED);
        }
    }

    private Account findAccount(Long userId) {
        return accountRepository
                .findAccountByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void sendAuditLog(Long userId, String action, String description, Map<String, Object> metadata) {
        auditProducer.auditLogAccount(
                AuditEvent.builder()
                        .userId(userId)
                        .serviceName("account-service")
                        .eventType("ACCOUNT")
                        .action(action)
                        .status("SUCCESS")
                        .description(description)
                        .metadata(metadata)
                        .build()
        );
    }
}
