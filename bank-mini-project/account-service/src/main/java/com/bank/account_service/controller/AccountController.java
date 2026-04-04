package com.bank.account_service.controller;

import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.service.AccountService;
import com.bank.bank_common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor

public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public AccountResponse getMyAccount() {
        Long userId = SecurityUtils.getCurrentUserId();
        System.out.println("userId = " + userId);
        return accountService.getUserIdByUserId(userId);
    }

    @GetMapping("/me/balance")
    public BigDecimal getMyBalance() {
        Long userId = SecurityUtils.getCurrentUserId();
        AccountResponse account = accountService.getUserIdByUserId(userId);

        return account.getBalance();
    }

}