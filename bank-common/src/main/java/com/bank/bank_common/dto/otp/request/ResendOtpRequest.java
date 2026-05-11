package com.bank.bank_common.dto.otp.request;

import com.bank.bank_common.dto.otp.OtpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendOtpRequest {
    private String identifier;
    private OtpType type;
}