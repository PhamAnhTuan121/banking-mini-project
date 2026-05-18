package com.bank.auth_service.controller;

import com.bank.auth_service.dto.request.*;
import com.bank.auth_service.dto.response.AuthResponse;
import com.bank.auth_service.service.AuthService;
import com.bank.bank_common.dto.account.response.ApiResponse;
import com.bank.bank_common.dto.auth.request.ChangeEmailRequest;
import com.bank.bank_common.dto.auth.request.VerifyChangeEmailRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.bank_common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/verify-register")
    public AuthResponse verifyPhone(@RequestBody VerifyOtpRequest request) {
        return authService.verifyPhone(request);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.changePassword(userId, request);
        return ApiResponse.success(
                "Password changed successfully"
        );
    }

    @PostMapping("/forgot-password/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam String phone) {
        authService.sendForgotPasswordOtp(phone);
        return ApiResponse.success(
                "OTP sent successfully"
        );
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success(
                "Password forgot successfully"
        );
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String identifier) {
        authService.resendOtp(identifier);
        return ResponseEntity.ok("Resend OTP success");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String header) {
        String refreshToken = header.substring(7);
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/phone/change/request")
    public ApiResponse<String> requestChangePhone(
            @RequestBody ChangePhoneRequest request
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.requestChangePhone(userId, request);
        return ApiResponse.success(
                "Otp sent successfully"
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/phone/change/verify")
    public ApiResponse<String> verifyChangePhone(
            @RequestBody VerifyChangePhoneRequest request
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.verifyChangePhone(userId, request);
        return ApiResponse.success(
                "Change phone successfully"
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/email/change/request")
    public ApiResponse<String> requestChangeEmail(@RequestBody ChangeEmailRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.requestChangeEmail(userId, request);
        return ApiResponse.success("OTP sent to new email successfully");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/email/change/verify")
    public ApiResponse<String> verifyChangeEmail(@RequestBody VerifyChangeEmailRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        authService.verifyChangeEmail(userId, request);
        return ApiResponse.success("Change email successfully");
    }
}
