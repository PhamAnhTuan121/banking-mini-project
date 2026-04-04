package com.bank.notification_service.client_wrapper;

import com.bank.bank_common.dto.auth.response.EmailResponse;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;

import com.bank.notification_service.client.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetEmail")
    public EmailResponse getEmailByUserId(Long userId) {
        return userClient.getEmailUserById(userId);
    }

    public EmailResponse fallbackGetEmail(Long userId, Throwable ex) {
        throw new BusinessException(ErrorCode.USER_SERVICE_UNAVAILABLE);
    }
}