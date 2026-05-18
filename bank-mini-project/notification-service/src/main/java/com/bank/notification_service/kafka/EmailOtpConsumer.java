package com.bank.notification_service.kafka;

import com.bank.bank_common.dto.event.EmailOtpEvent;
import com.bank.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailOtpConsumer {

    private final NotificationService notificationService;


    @KafkaListener(topics = "send-email-otp-topic")
    public void consume(EmailOtpEvent event){
        log.info("Received email otp event: " + event);
        notificationService.sendOtpEmail(
                event.getToEmail(),
                event.getOtp()
        );
    }
}
