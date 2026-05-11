package com.bank.notification_service.service.impl;

import com.bank.bank_common.dto.notification.request.NotificationRequest;
import com.bank.notification_service.dto.response.NotificationResponse;
import com.bank.notification_service.mapper.NotificationMapper;
import com.bank.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse sendEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getToEmail());
            message.setSubject(request.getSubject());
            message.setText(request.getMessage());
            mailSender.send(message);
            return notificationMapper.toResponse(request);
        } catch (Exception e) {
            throw new RuntimeException("Send email failed", e);
        }
    }
}
