package com.bank.bank_common.dto.auth.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEmailRequest {

    private String newEmail;
}