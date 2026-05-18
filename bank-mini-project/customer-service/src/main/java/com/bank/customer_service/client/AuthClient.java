package com.bank.customer_service.client;

import com.bank.bank_common.config.InternalFeignConfig;
import com.bank.bank_common.dto.customer.request.UpdatePhoneRequest;
import com.bank.bank_common.dto.user.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service" , configuration = InternalFeignConfig.class)
public interface AuthClient {

    @PutMapping("/api/v1/internal/users/{userId}/phone")
    void updatePhone(@PathVariable("userId") Long userId, @RequestBody UpdatePhoneRequest request);

    @GetMapping("/api/v1/internal/users/get/{userId}/phone")
    String getPhone(@PathVariable Long userId);

    @GetMapping("/api/v1/internal/users/get/user/{userId}")
    UserResponse getUser(@PathVariable Long userId);
}