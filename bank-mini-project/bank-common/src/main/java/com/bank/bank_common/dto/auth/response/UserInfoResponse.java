package com.bank.bank_common.dto.auth.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private String fullName;

    private String email;

}
