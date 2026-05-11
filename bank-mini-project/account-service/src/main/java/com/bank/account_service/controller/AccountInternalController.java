package com.bank.account_service.controller;


import com.bank.account_service.service.AccountService;
import com.bank.bank_common.dto.account.request.DepositRequest;
import com.bank.bank_common.dto.account.request.WithdrawRequest;
import com.bank.bank_common.dto.account.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/accounts")
@RequiredArgsConstructor
public class AccountInternalController {

    private final AccountService accountService;

    @PreAuthorize("hasRole('INTERNAL')")
    @PutMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(
            @PathVariable String accountNumber,
            @RequestBody DepositRequest request) {

        return accountService.deposit(accountNumber, request.getAmount());
    }

    @PreAuthorize("hasAnyRole('INTERNAL','ADMIN')")
    @PutMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(
            @PathVariable String accountNumber,
            @RequestBody WithdrawRequest request) {

        return accountService.withdraw(accountNumber, request.getAmount());
    }
    @GetMapping("/user/{userId}")
    AccountResponse getUserId(@PathVariable Long userId) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        System.out.println("AUTH: " + auth);

        return accountService.getUserIdByUserId(userId);
    }

    @PreAuthorize("hasAnyRole('INTERNAL','ADMIN')")
    @GetMapping("/account-number/{accountNumber}")
    AccountResponse getAccountByUserId(@PathVariable("accountNumber") String accountNumber) {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        System.out.println("AUTH = " + auth);
        return accountService.getAccountByNumber(accountNumber);
    }

}
