package com.bank.notification_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private String toEmail;
    private String status;
    private String detail;
}
