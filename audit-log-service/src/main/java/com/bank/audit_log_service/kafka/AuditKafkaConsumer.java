package com.bank.audit_log_service.kafka;


import com.bank.audit_log_service.service.AuditLogService;
import com.bank.bank_common.dto.audit_log.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditKafkaConsumer {

    private final AuditLogService auditLogService;

    @KafkaListener(topics = "audit-log-topic")
    public void consume(AuditEvent event) {
        log.info("Received event: {}", event);
        auditLogService.saveAuditLog(event);
    }

}
