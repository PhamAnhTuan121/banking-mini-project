package com.bank.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String message;
    private int status;
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
}