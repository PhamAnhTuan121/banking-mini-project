package com.bank.customer_service.client;

import com.bank.bank_common.config.InternalFeignConfig;

import com.bank.bank_common.dto.account.response.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", configuration = InternalFeignConfig.class)
public interface AccountClient {

    @GetMapping("/api/v1/internal/accounts/user/{userId}")
    AccountResponse getByUserId(@PathVariable Long userId);
}