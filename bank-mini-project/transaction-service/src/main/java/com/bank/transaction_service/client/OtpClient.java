package com.bank.transaction_service.client;

import com.bank.bank_common.config.InternalFeignConfig;
import com.bank.bank_common.dto.otp.request.SendOtpRequest;
import com.bank.bank_common.dto.otp.request.VerifyOtpRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "otp-service", path = "/otp" , configuration = InternalFeignConfig.class)
public interface OtpClient {

    @PostMapping("/send")
    void send(@RequestBody SendOtpRequest request);

    @PostMapping("/verify")
    void verify(@RequestBody VerifyOtpRequest request);

}
