package com.bank.bank_common.client;

import com.bank.bank_common.config.InternalFeignConfig;
import com.bank.bank_common.dto.otp.request.ResendOtpRequest;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "otp-service", configuration = InternalFeignConfig.class)
@Configuration
public interface OtpClient {

    @PostMapping("/otp/send")
    void sendOtp(@RequestBody SendOtpRequest request);

    @PostMapping("/otp/verify")
    void verifyOtp(@RequestBody VerifyOtpRequest request);

    @PostMapping("/otp/resend")
    void resend(@Valid @RequestBody ResendOtpRequest request);
}