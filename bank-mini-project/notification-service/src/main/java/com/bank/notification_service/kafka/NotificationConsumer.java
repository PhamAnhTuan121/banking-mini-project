package com.bank.notification_service.kafka;

import com.bank.bank_common.dto.auth.response.UserInfoResponse;
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
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(TransactionSuccessEvent event) {

        log.info("📥 Received event: {}", event);

        try {

            // ===== USER INFO =====

            UserInfoResponse sender =
                    userService.getUserById(
                            event.getSenderUserId()
                    );

            UserInfoResponse receiver =
                    userService.getUserById(
                            event.getReceiverUserId()
                    );

            // ===== EMAIL CONTENT =====

            String senderContent = buildSenderContent(
                    event,
                    receiver.getFullName()
            );

            String receiverContent = buildReceiverContent(
                    event,
                    sender.getFullName()
            );

            // ===== SEND SENDER =====

            notificationService.sendEmail(

                    new NotificationRequest(

                            sender.getEmail(),

                            "Transfer Successful",

                            senderContent,

                            "TRANSACTION_SUCCESS"
                    )
            );

            // ===== SEND RECEIVER =====

            notificationService.sendEmail(

                    new NotificationRequest(

                            receiver.getEmail(),

                            "Money Received",

                            receiverContent,

                            "TRANSACTION_SUCCESS"
                    )
            );

        } catch (Exception ex) {

            log.error("❌ Failed to process event", ex);

            throw ex;
        }
    }

    // =====================================
    // SENDER EMAIL
    // =====================================

    private String buildSenderContent(
            TransactionSuccessEvent event,
            String receiverName
    ) {

        return """

                Dear Customer,

                Your transaction was completed successfully.

                ====================================

                Transaction Time: %s

                From Account: %s

                Beneficiary Account: %s

                Beneficiary Name: %s

                Bank: %s

                Amount: %s VND

                Description: %s

                ====================================

                Thank you for using our banking service.

                """
                .formatted(
                        event.getTransactionTime(),
                        event.getFromAccount(),
                        event.getToAccount(),
                        receiverName,
                        event.getBankName(),
                        event.getAmount(),
                        event.getDescription()
                );
    }

    // =====================================
    // RECEIVER EMAIL
    // =====================================

    private String buildReceiverContent(
            TransactionSuccessEvent event,
            String senderName
    ) {

        return """

                Dear Customer,

                You have received money successfully.

                ====================================

                Transaction Time: %s

                Sender Account: %s

                Sender Name: %s

                Bank: %s

                Amount: %s VND

                Description: %s

                ====================================

                Thank you for using our banking service.

                """
                .formatted(
                        event.getTransactionTime(),
                        event.getFromAccount(),
                        senderName,
                        event.getBankName(),
                        event.getAmount(),
                        event.getDescription()
                );
    }
}