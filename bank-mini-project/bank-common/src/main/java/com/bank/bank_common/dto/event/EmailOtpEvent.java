package com.bank.bank_common.dto.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtpEvent {

    private String toEmail;

    private String otp;

    private String subject;

    private String content;
}