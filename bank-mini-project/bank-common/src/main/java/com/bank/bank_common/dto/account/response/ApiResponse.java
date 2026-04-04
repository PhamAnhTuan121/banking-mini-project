package com.bank.bank_common.dto.account.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private int code;

    private String message;

    private T result;
}