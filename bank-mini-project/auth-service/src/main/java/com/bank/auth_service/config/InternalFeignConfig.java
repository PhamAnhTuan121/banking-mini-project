package com.bank.auth_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class InternalFeignConfig {

    @Value("${internal.header}")
    private String header;

    @Value("${internal.secret}")
    private String secret;

    @Bean
    public RequestInterceptor internalInterceptor() {
        return template -> {
            template.header(header, secret);
        };
    }

}