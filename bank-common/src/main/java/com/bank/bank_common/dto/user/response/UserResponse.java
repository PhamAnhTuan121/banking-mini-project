package com.bank.bank_common.dto.user.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String roleName;
    private String status;
}