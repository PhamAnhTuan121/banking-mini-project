package com.bank.auth_service.dto.response;

import com.bank.bank_common.dto.user.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String message;
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
}