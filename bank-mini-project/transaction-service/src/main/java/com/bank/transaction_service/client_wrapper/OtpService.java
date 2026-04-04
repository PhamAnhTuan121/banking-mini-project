package com.bank.transaction_service.client_wrapper;

import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.transaction_service.client.OtpClient;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpClient otpClient;

    // ================= SEND OTP =================

    @Retry(name = "otpService")
    public void sendOtp(String correlationId, OtpType otpType) {
        try {
            otpClient.send(
                    new SendOtpRequest(correlationId, otpType)
            );
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.OTP_SERVICE_UNAVAILABLE);
        }
    }

    // ================= VERIFY OTP =================

    @Retry(name = "otpService")
    public void verifyOtp(String correlationId, String otp, OtpType otpType) {

        if (otp == null || otp.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.OTP_INVALID);
        }

        try {
            otpClient.verify(
                    new VerifyOtpRequest(correlationId, otp.trim(), otpType)
            );

        } catch (BusinessException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.OTP_SERVICE_UNAVAILABLE);
        }
    }
}