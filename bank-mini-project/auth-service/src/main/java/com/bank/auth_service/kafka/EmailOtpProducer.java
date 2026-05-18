package com.bank.auth_service.kafka;

import com.bank.bank_common.dto.event.EmailOtpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailOtpProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC =
            "send-email-otp-topic";

    public void sendOtp(
            String toEmail,
            String otp
    ) {

        EmailOtpEvent event =
                EmailOtpEvent.builder()
                        .toEmail(toEmail)
                        .otp(otp)
                        .subject("Verify Your New Email")
                        .content(
                                "Your OTP code is: "
                                        + otp
                                        + "\n\nThis code expires in 5 minutes."
                        )
                        .build();

        kafkaTemplate.send(
                TOPIC,
                event
        );
    }
}