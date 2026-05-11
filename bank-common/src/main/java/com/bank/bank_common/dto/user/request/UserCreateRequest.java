package com.bank.bank_common.dto.user.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
}