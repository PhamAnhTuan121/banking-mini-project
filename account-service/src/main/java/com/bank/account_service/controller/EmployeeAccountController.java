package com.bank.account_service.controller;

import com.bank.account_service.service.AccountService;
import com.bank.bank_common.dto.account.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee/accounts")
@RequiredArgsConstructor
public class EmployeeAccountController {

    private final AccountService accountService;

    @PostMapping("/{userId}/freeze")
    public ApiResponse<Void> freezeAccount(@PathVariable Long userId) {
        accountService.freezeAccount(userId);
        return ApiResponse.<Void>builder()
                .message("Account frozen successfully")
                .build();
    }

    @PostMapping("/{userId}/close")
    public ApiResponse<Void> closeAccount(@PathVariable Long userId) {
        accountService.closeAccount(userId);
        return ApiResponse.<Void>builder()
                .message("Account closed successfully")
                .build();
    }

}
