package com.bank.bank_common.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class InternalFeignConfig {

    private final String serviceName;

    public InternalFeignConfig(
            @Value("${spring.application.name}") String serviceName
    ) {
        this.serviceName = serviceName;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("X-INTERNAL-SOURCE", serviceName);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new BaseFeignErrorDecoder();
    }
}