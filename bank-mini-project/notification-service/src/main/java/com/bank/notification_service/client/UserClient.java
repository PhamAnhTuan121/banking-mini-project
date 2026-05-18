package com.bank.notification_service.client;

import com.bank.bank_common.dto.auth.response.EmailResponse;
import com.bank.bank_common.dto.auth.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/api/v1/internal/users/{id}/email")
    EmailResponse getEmailUserById(@PathVariable Long id);


    @GetMapping("/api/v1//internal/users/{id}")
    UserInfoResponse getUserById(
            @PathVariable Long id
    );

}
