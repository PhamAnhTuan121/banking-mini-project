package com.bank.auth_service.kafka;

import com.bank.bank_common.dto.event.StatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class StatusProducer {

    private final KafkaTemplate<String, StatusEvent> kafkaTemplate;

    public void sendBlockAccount(StatusEvent event) {
        try {
            kafkaTemplate.send("status-events", event);
            log.info("Block sent to Kafka");
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendUnblockAccount(StatusEvent event) {
        try {
            kafkaTemplate.send("status-events", event);
            log.info("Unblock sent to Kafka");
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendUserUpdated(StatusEvent event) {
        try {
            kafkaTemplate.send("update-phone-event", event);
            log.info("Update phone sent to Kafka");
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
