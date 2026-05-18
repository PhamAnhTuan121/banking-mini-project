package com.bank.notification_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private String toEmail;
    private String status;
    private String detail;
    private String subject;
    private String message;
}
