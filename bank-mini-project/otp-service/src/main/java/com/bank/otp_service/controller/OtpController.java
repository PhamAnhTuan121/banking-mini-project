package com.bank.otp_service.controller;

import com.bank.bank_common.dto.otp.request.ResendOtpRequest;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.otp_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@PreAuthorize("hasRole('INTERNAL')")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody SendOtpRequest request) {

        otpService.sendOtp(
                request.getIdentifier(),
                request.getType()
        );

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(
                request.getIdentifier(),
                request.getOtp(),
                request.getType()
        );

        return ResponseEntity.ok("OTP verified successfully");
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@Valid @RequestBody ResendOtpRequest request) {

        otpService.resendOtp(
                request.getIdentifier(),
                request.getType()
        );

        return ResponseEntity.ok("OTP resent successfully");
    }
}