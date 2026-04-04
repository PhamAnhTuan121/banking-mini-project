package com.bank.auth_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    @JsonProperty("fullName")
    private String fullName;
    private String phone;
    private String email;
    private String roleName;
}