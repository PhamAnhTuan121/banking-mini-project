package com.bank.account_service.service;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.bank_common.dto.account.response.AccountResponse;

import java.math.BigDecimal;

public interface AccountService {
    AccountResponse createAccount(AccountCreateRequest request);

    AccountResponse getAccountByNumber(String accountNumber);

    AccountResponse deposit(String accountNumber, BigDecimal amount);

    AccountResponse withdraw(String accountNumber, BigDecimal amount);

    AccountResponse getUserIdByUserId(Long userId);

    void blockAccount(Long userId);

    void unblockAccount(Long userId);

    void freezeAccount(Long userId);

    void unfreezeAccount(Long userId);

    void closeAccount(Long userId);

}
