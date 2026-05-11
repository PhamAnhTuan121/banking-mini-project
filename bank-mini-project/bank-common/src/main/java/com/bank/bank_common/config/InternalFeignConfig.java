package com.bank.bank_common.config;

import com.bank.bank_common.constant.SecurityConstants;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor internalRequestInterceptor() {

        return template -> {

            template.header(
                    SecurityConstants.HEADER_INTERNAL,
                    SecurityConstants.INTERNAL_SECRET
            );

            template.header(
                    "X-Internal-Source",
                    "internal-service"
            );
        };
    }

    @Bean
    public ErrorDecoder errorDecoder(
            BaseFeignErrorDecoder decoder
    ) {
        return decoder;
    }
}