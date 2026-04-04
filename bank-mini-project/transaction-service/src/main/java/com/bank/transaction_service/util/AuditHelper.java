package com.bank.transaction_service.util;

import com.bank.bank_common.dto.audit_log.AuditEvent;
import com.bank.transaction_service.kafka.AuditLogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditHelper {

    private final AuditLogProducer auditLogProducer;

    private static final String SERVICE_NAME = "transaction-service";

    // ===== SUCCESS =====
    public void success(Long userId,
                        String eventType,
                        String action,
                        String description,
                        Map<String, Object> metadata) {

        send(userId, eventType, action, "SUCCESS", description, metadata);
    }

    // ===== FAIL =====
    public void fail(Long userId,
                     String eventType,
                     String action,
                     String description,
                     Map<String, Object> metadata) {

        send(userId, eventType, action, "FAILED", description, metadata);
    }

    // ===== CORE =====
    private void send(Long userId,
                      String eventType,
                      String action,
                      String status,
                      String description,
                      Map<String, Object> metadata) {

        AuditEvent event = AuditEvent.builder()
                .userId(userId)
                .username(null)
                .serviceName(SERVICE_NAME)
                .eventType(eventType)   // 🔥 quan trọng
                .action(action)
                .status(status)
                .description(description)
                .requestId(generateRequestId())
                .metadata(metadata)
                .build();

        auditLogProducer.sendAudit(event);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}