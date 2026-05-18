package com.bank.transaction_service.kafka;

import com.bank.bank_common.dto.event.TransactionSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, TransactionSuccessEvent> kafkaTemplate;

    private final String TOPIC = "notification-topic";

    public void sendNotification(TransactionSuccessEvent event) {
        kafkaTemplate.send(TOPIC, event.getCorrelationId(), event);
        log.info("📤 Sent NotificationRequest to Kafka: {}", event);
    }
}
