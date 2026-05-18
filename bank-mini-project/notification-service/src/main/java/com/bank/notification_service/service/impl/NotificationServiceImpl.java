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

    @Override
    public NotificationResponse sendOtpEmail(
            String toEmail,
            String otp
    ) {

        try {

            String subject =
                    "Verify Your New Email";

            String content =
                    "Your OTP code is: "
                            + otp
                            + "\n\nThis code expires in 5 minutes.";

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            return NotificationResponse.builder()
                    .toEmail(toEmail)
                    .subject(subject)
                    .message("OTP email sent successfully")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Send OTP email failed",
                    e
            );
        }
    }
}
