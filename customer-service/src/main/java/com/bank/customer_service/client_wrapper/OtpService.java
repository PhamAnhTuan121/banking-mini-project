package com.bank.customer_service.client_wrapper;

import com.bank.bank_common.client.OtpClient;
import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import com.bank.bank_common.exception.BusinessException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpClient otpClient;

    @Retry(name = "otpService")
    public void sendOtp(SendOtpRequest request){
        try {
            otpClient.sendOtp(request);
        }catch (BusinessException ex){
            throw ex;
        }catch (Exception ex){
            throw ex;
        }
    }

    public void verifyOtp(String identifier, String otp, OtpType type) {
        try {
            otpClient.verifyOtp(new VerifyOtpRequest(identifier, otp, type));
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
    }


}
