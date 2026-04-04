package com.bank.notification_service.controller;

import com.bank.bank_common.dto.notification.request.NotificationRequest;
import com.bank.notification_service.dto.response.BaseResponse;
import com.bank.notification_service.dto.response.NotificationResponse;
import com.bank.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JavaMailSender mailSender;

    @PostMapping("/send")
    public ResponseEntity<BaseResponse<NotificationResponse>> send(
            @Valid @RequestBody NotificationRequest request) {

        NotificationResponse response = notificationService.sendEmail(request);
        return ResponseEntity.ok(
                new BaseResponse<>(200, "Notification sent successfully", response)
        );
    }

    @GetMapping("/test")
    public String testMail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("phamtuan785bb@gmail.com");
        msg.setSubject("Test");
        msg.setText("Mailtrap working!");
        mailSender.send(msg);
        return "OK";
    }

}
