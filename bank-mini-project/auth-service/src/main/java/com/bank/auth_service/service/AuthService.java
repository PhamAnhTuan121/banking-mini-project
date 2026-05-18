package com.bank.auth_service.service;

import com.bank.auth_service.dto.request.*;
import com.bank.auth_service.dto.response.AuthResponse;
import com.bank.bank_common.dto.auth.request.ChangeEmailRequest;
import com.bank.bank_common.dto.auth.request.VerifyChangeEmailRequest;
import com.bank.bank_common.dto.auth.response.RegisterRequestPhoneResponse;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;

public interface AuthService {
     RegisterRequestPhoneResponse register(RegisterRequest registerRequest);
     AuthResponse login(LoginRequest loginRequest);
     AuthResponse refresh(String refreshToken);
     AuthResponse verifyPhone(VerifyOtpRequest request);

     void resendOtp(String identiier);

     void blockUser(Long userId);
     void unblockUser(Long userId);

     void changePassword(Long userId, ChangePasswordRequest request);

     void forgotPassword(ForgotPasswordRequest request);

     void sendForgotPasswordOtp(String phone);

     void requestChangePhone(Long userId, ChangePhoneRequest request);

     void verifyChangePhone(Long userId,
                            VerifyChangePhoneRequest request);

     void requestChangeEmail(
             Long userId,
             ChangeEmailRequest request
     );

     void verifyChangeEmail(
             Long userId,
             VerifyChangeEmailRequest request
     );
}
