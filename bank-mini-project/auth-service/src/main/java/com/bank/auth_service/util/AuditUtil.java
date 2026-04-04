package com.bank.auth_service.util;

import com.bank.auth_service.kafka.AuditProducer;
import com.bank.bank_common.dto.audit_log.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditUtil {

    private final AuditProducer auditProducer;
    private static final String SERVICE = "auth-service";

    public void success(Long userId, String username,
                        String eventType,
                        String action,
                        String desc) {

        auditProducer.sendAudit(
                AuditEvent.builder()
                        .userId(userId)
                        .username(username)
                        .serviceName(SERVICE)
                        .eventType(eventType)   // 🔥 quan trọng
                        .action(action)
                        .status("SUCCESS")
                        .description(desc)
                        .requestId(generateRequestId()) // 🔥 trace
                        .metadata(null)
                        .build()
        );
    }

    public void fail(Long userId, String username,
                     String eventType,
                     String action,
                     String desc) {

        auditProducer.sendAudit(
                AuditEvent.builder()
                        .userId(userId)
                        .username(username)
                        .serviceName(SERVICE)
                        .eventType(eventType)
                        .action(action)
                        .status("FAILED")
                        .description(desc)
                        .requestId(generateRequestId())
                        .metadata(null)
                        .build()
        );
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}