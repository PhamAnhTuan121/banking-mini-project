package com.bank.transaction_service.client_wrapper;

import com.bank.bank_common.dto.account.request.DepositRequest;
import com.bank.bank_common.dto.account.request.WithdrawRequest;

import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.transaction_service.client.AccountClient;
import com.bank.transaction_service.dto.transaction.response.AccountResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountClient accountClient;

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackGetAccountByUserId")
    public AccountResponse getAccountByUserId(Long userId) {
        return accountClient.getAccountNumberByUserId(userId);
    }

    public AccountResponse fallbackGetAccountByUserId(Long userId, Throwable ex) {
        System.out.println("loi 1 " + ex.getMessage());
        throw new BusinessException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackGetByAccountNumber")
    public AccountResponse getByAccountNumber(String accountNumber) {
        return accountClient.getAccountByAccountNumber(accountNumber);
    }

    public AccountResponse fallbackGetByAccountNumber(String accountNumber, Throwable ex) {
        System.out.println("loi 2 " +ex.getMessage());
        throw new BusinessException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackWithdraw")
    public void withdraw(String account, WithdrawRequest request) {
        accountClient.withdraw(account, request);
    }

    public void fallbackWithdraw(String account, WithdrawRequest request, Throwable ex) {
        System.out.println( "loi 3 " +ex.getMessage());
        throw new BusinessException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackDeposit")
    public void deposit(String account, DepositRequest request) {
        accountClient.deposit(account, request);
    }

    public void fallbackDeposit(String account, DepositRequest request, Throwable ex) {
        System.out.println( "loi 4 " +ex.getMessage());
        throw new BusinessException(ErrorCode.ACCOUNT_SERVICE_UNAVAILABLE);
    }
}
