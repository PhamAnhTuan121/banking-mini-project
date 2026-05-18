package com.bank.bank_common.dto.auth.request;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyChangeEmailRequest {

    private String otp;

}
