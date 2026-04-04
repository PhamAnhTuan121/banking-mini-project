package com.bank.account_service.controller;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountResponse createAccount(@RequestBody AccountCreateRequest request) {
        return accountService.createAccount(request);
    }

    @PutMapping("/{userId}/freeze")
    public void freezeAccount(@PathVariable Long userId) {
        accountService.freezeAccount(userId);
    }

    @PutMapping("/{userId}/unfreeze")
    public void unfreezeAccount(@PathVariable Long userId) {
        accountService.unfreezeAccount(userId);
    }
}