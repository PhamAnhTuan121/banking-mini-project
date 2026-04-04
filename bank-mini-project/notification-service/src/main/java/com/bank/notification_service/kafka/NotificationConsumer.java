package com.bank.notification_service.kafka;

import com.bank.bank_common.dto.event.TransactionSuccessEvent;
import com.bank.bank_common.dto.notification.request.NotificationRequest;
import com.bank.notification_service.client_wrapper.UserService;
import com.bank.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final UserService userService;

    @KafkaListener(
            topics = "transaction-success-topic",
            groupId = "notification-group"
    )
    public void consume(TransactionSuccessEvent event) {

        log.info("📥 Received event: {}", event);

        try {
            // lấy email
            String senderEmail =
                    userService.getEmailByUserId(event.getSenderUserId()).getEmail();

            String receiverEmail =
                    userService.getEmailByUserId(event.getReceiverUserId()).getEmail();

            // gửi mail sender
            notificationService.sendEmail(
                    new NotificationRequest(
                            senderEmail,
                            "Transfer Successful",
                            "You transferred " + event.getAmount(),
                            "TRANSACTION_SUCCESS"
                    )
            );

            // gửi mail receiver
            notificationService.sendEmail(
                    new NotificationRequest(
                            receiverEmail,
                            "Transfer Received",
                            "You received " + event.getAmount(),
                            "TRANSACTION_SUCCESS"
                    )
            );

        } catch (Exception ex) {
            log.error("❌ Failed to process event", ex);

            // 💣 QUAN TRỌNG → để Kafka retry
            throw ex;
        }
    }
}