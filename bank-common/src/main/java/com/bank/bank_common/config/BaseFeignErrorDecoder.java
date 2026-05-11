package com.bank.bank_common.config;

import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.bank_common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BaseFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultDecoder = new Default();

    public BaseFeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response == null || response.body() == null) {
            return new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        try {
            String body = new String(
                    response.body().asInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            if (body.isBlank()) {
                return new BusinessException(ErrorCode.INTERNAL_ERROR);
            }

            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);

            return mapErrorCode(error != null ? error.getCode() : null);

        } catch (IOException e) {
            return new BusinessException(ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            return defaultDecoder.decode(methodKey, response);
        }
    }

    private BusinessException mapErrorCode(String code) {

        if (code == null) {
            return new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        for (ErrorCode ec : ErrorCode.values()) {
            if (ec.getCode().equals(code)) {
                return new BusinessException(ec);
            }
        }

        return new BusinessException(ErrorCode.INTERNAL_ERROR);
    }
}