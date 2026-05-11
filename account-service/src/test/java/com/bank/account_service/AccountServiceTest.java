package com.bank.account_service;

import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Status;
import com.bank.account_service.kafka.AuditLogAccountProducer;
import com.bank.account_service.mapper.AccountMapper;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.impl.AccountServiceImpl;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AuditLogAccountProducer auditProducer;

    @InjectMocks
    private AccountServiceImpl accountService;

    // =======================
    // 1. TEST DEPOSIT SUCCESS
    // =======================
    @Test
    void deposit_shouldSuccess() {
        Account account = new Account();
        account.setAccountNumber("123");
        account.setBalance(new BigDecimal("1000"));
        account.setStatus(Status.ACTIVE);

        when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.of(account));

        accountService.deposit("123", new BigDecimal("200"));

        assertEquals(new BigDecimal("1200"), account.getBalance());
        verify(accountRepository, times(2)).save(account);
    }

    // =======================
    // 2. TEST INVALID AMOUNT
    // =======================
    @Test
    void deposit_shouldFail_whenAmountInvalid() {
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            accountService.deposit("123", BigDecimal.ZERO);
        });

        assertEquals(ErrorCode.INVALID_AMOUNT, ex.getErrorCode());
    }

    // =======================
    // 3. TEST ACCOUNT NOT FOUND
    // =======================
    @Test
    void deposit_shouldFail_whenAccountNotFound() {
        when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            accountService.deposit("123", new BigDecimal("100"));
        });
    }

    // =======================
    // 4. TEST WITHDRAW FAIL (NOT ENOUGH MONEY)
    // =======================
    @Test
    void withdraw_shouldFail_whenInsufficientBalance() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100"));
        account.setStatus(Status.ACTIVE);

        when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.of(account));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            accountService.withdraw("123", new BigDecimal("200"));
        });

        assertEquals(ErrorCode.INSUFFICIENT_BALANCE, ex.getErrorCode());
    }

    // =======================
    // 5. TEST BLOCK ACCOUNT SUCCESS
    // =======================
    @Test
    void blockAccount_shouldSuccess() {
        Account account = new Account();
        account.setUserId(1L);
        account.setStatus(Status.ACTIVE);
        account.setAccountNumber("123");

        when(accountRepository.findAccountByUserId(1L))
                .thenReturn(Optional.of(account));

        accountService.blockAccount(1L);

        assertEquals(Status.BLOCKED, account.getStatus());
        verify(accountRepository).save(account);
        verify(auditProducer).auditLogAccount(any());
    }

    // =======================
    // 6. TEST BLOCK ACCOUNT FAIL (ALREADY BLOCKED)
    // =======================
    @Test
    void blockAccount_shouldFail_whenAlreadyBlocked() {
        Account account = new Account();
        account.setStatus(Status.BLOCKED);

        when(accountRepository.findAccountByUserId(1L))
                .thenReturn(Optional.of(account));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            accountService.blockAccount(1L);
        });

        assertEquals(ErrorCode.ACCOUNT_ALREADY_BLOCKED, ex.getErrorCode());
    }

    // =======================
    // 7. TEST CLOSE ACCOUNT FAIL (BALANCE > 0)
    // =======================
    @Test
    void closeAccount_shouldFail_whenBalanceNotZero() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100"));
        account.setStatus(Status.ACTIVE);

        when(accountRepository.findAccountByUserId(1L))
                .thenReturn(Optional.of(account));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            accountService.closeAccount(1L);
        });

        assertEquals(ErrorCode.ACCOUNT_BALANCE_NOT_ZERO, ex.getErrorCode());
    }
}