package com.bank.account_service.exception;

import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                LocalDateTime.now().toString(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(response);
    }

}
