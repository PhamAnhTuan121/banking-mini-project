package com.bank.auth_service.kafka;

import com.bank.bank_common.dto.audit_log.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditProducer {

        private final KafkaTemplate<String , AuditEvent> kafkaTemplate;

    private static final String TOPIC = "audit-log-topic";

    public void sendAudit(AuditEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }

}
