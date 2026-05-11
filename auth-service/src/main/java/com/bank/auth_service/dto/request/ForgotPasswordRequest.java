package com.bank.auth_service.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String identifier;
    private String otp;
    private String newPassword;
}