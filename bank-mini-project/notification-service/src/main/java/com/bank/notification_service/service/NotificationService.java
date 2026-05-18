package com.bank.notification_service.service;

import com.bank.bank_common.dto.notification.request.NotificationRequest;

import com.bank.notification_service.dto.response.NotificationResponse;

public interface NotificationService {
    NotificationResponse sendEmail(NotificationRequest request);
    NotificationResponse sendOtpEmail(String toEmail , String otp);
}
