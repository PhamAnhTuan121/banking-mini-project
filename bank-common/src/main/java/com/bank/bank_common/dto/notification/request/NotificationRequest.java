package com.bank.bank_common.dto.notification.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    @Email(message = "Invalid email format")
    private String toEmail;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message body is required")
    private String message;

    private String type; // e.g. TRANSACTION_SUCCESS, ACCOUNT_BLOCKED
}
