package com.bank.auth_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;

    @NotNull(message = "Full name is required")
    @JsonProperty("fullName")
    private String fullName;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^(0|84)[0-9]{9}$", message = "Invalid phone number format")
    private String phone;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

}