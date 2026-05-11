package com.bank.account_service.kafka;

import com.bank.bank_common.dto.audit_log.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogAccountProducer {

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    public void auditLogAccount(AuditEvent  event ) {
        kafkaTemplate.send("audit-log-topic", event);
    }
}
