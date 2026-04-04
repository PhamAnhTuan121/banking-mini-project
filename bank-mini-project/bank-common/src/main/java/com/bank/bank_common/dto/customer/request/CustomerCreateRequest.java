package com.bank.bank_common.dto.customer.request;

import lombok.Data;

@Data
public class CustomerCreateRequest {
    private Long userId;     // id từ auth-service
    private String fullName; // lấy từ register
    private String phone;
}
