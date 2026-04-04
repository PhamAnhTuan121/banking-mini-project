package com.bank.notification_service.config;

import com.bank.bank_common.config.BaseFeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new BaseFeignErrorDecoder();
    }
}
