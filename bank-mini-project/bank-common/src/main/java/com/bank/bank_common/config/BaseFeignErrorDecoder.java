package com.bank.bank_common.config;

import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.bank_common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class BaseFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        try {
            if (response.body() == null) {
                return new BusinessException(ErrorCode.INTERNAL_ERROR);
            }

            String body = new String(response.body().asInputStream().readAllBytes());

            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);

            // 🔥 FIX: map theo code string
            return mapErrorCode(error.getCode());

        } catch (Exception e) {
            e.printStackTrace();
            return new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private BusinessException mapErrorCode(String code) {

        for (ErrorCode ec : ErrorCode.values()) {
            if (ec.getCode().equals(code)) {
                return new BusinessException(ec);
            }
        }

        return new BusinessException(ErrorCode.INTERNAL_ERROR);
    }
}
