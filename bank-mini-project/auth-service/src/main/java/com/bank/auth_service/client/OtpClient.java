package com.bank.auth_service.client;

import com.bank.bank_common.config.InternalFeignConfig;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "otp-service" , configuration = InternalFeignConfig.class)
public interface OtpClient {

    @PostMapping("/otp/send")
    void sendOtp(@RequestBody SendOtpRequest request);

    @PostMapping("/otp/verify")
    void verifyOtp(@RequestBody VerifyOtpRequest request);

}
