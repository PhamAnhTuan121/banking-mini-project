package com.bank.transaction_service.client;

import com.bank.bank_common.config.InternalFeignConfig;
import com.bank.bank_common.dto.account.request.DepositRequest;
import com.bank.bank_common.dto.account.request.WithdrawRequest;
import com.bank.transaction_service.dto.transaction.response.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "ACCOUNT-SERVICE" , configuration = InternalFeignConfig.class)
public interface AccountClient {

    @PutMapping("/api/v1/internal/accounts/{accountNumber}/withdraw")
    AccountResponse withdraw(
            @PathVariable String accountNumber,
            @RequestBody WithdrawRequest request
    );

    @PutMapping("/api/v1/internal/accounts/{accountNumber}/deposit")
    AccountResponse deposit(
            @PathVariable String accountNumber,
            @RequestBody DepositRequest request
    );

    @GetMapping("/api/v1/internal/accounts/account-number/{accountNumber}")
    AccountResponse getAccountByAccountNumber(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/api/v1/accounts/{accountNumber}/balance")
    BigDecimal getBalance(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/api/v1/internal/accounts/user/{userId}")
    AccountResponse getAccountNumberByUserId(@PathVariable Long userId);
}
