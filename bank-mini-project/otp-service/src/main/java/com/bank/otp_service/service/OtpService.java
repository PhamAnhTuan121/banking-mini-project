package com.bank.otp_service.service;

import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.exception.BusinessException;

public interface OtpService {
    void sendOtp(String identifier , OtpType otpType);
    void verifyOtp(String  identifier,String otp , OtpType otpType) throws BusinessException;
    void resendOtp(String identifier , OtpType otpType) throws BusinessException;
}
