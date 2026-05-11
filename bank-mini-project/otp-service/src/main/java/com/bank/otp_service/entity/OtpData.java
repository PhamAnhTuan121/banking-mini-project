package com.bank.otp_service.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpData {
    private String code;
}