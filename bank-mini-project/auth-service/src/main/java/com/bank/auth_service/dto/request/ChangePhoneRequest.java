package com.bank.auth_service.dto.request;

import lombok.Data;

@Data
public class ChangePhoneRequest {
    private String newPhone;
}