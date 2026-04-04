package com.bank.auth_service.service;

import com.bank.auth_service.dto.request.LoginRequest;
import com.bank.auth_service.dto.request.RegisterRequest;
import com.bank.auth_service.dto.response.AuthResponse;
import com.bank.bank_common.dto.auth.response.RegisterRequestPhoneResponse;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;

public interface AuthService {
     RegisterRequestPhoneResponse register(RegisterRequest registerRequest);
     AuthResponse login(LoginRequest loginRequest);
     AuthResponse refresh(String refreshToken);
     AuthResponse verifyPhone(VerifyOtpRequest request);

     void blockUser(Long userId);
     void unblockUser(Long userId);
}
