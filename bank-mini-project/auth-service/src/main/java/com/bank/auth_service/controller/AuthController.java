package com.bank.auth_service.controller;

import com.bank.auth_service.dto.request.LoginRequest;
import com.bank.auth_service.dto.request.RegisterRequest;
import com.bank.auth_service.dto.response.AuthResponse;
import com.bank.auth_service.service.AuthService;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/verify-register")
    public AuthResponse verifyPhone(@RequestBody VerifyOtpRequest request) {
        return authService.verifyPhone(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Bearer token");
        }
        String refreshToken = header.substring(7);
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
