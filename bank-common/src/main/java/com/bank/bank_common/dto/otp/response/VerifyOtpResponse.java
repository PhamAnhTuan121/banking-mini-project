package com.bank.bank_common.dto.otp.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyOtpResponse {
    private boolean verified;
}
