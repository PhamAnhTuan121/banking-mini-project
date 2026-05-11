package com.bank.bank_common.dto.account.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private int code;

    private String message;

    private T result;

    // ================= SUCCESS =================

    public static <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Success")
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Success")
                .build();
    }

    // ================= ERROR =================

    public static <T> ApiResponse<T> error(
            int code,
            String message
    ) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}