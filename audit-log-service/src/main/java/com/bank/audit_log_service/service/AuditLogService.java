package com.bank.audit_log_service.service;

import com.bank.audit_log_service.entity.AuditLog;
import com.bank.audit_log_service.repository.AuditLogRepository;
import com.bank.bank_common.dto.audit_log.AuditEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveAuditLog(AuditEvent event) {

        String metadataJson = null;

        try {
            if (event.getMetadata() != null) {
                metadataJson = objectMapper.writeValueAsString(event.getMetadata());
            }
        } catch (Exception e) {
            metadataJson = "{}"; // fallback
        }

        AuditLog log = AuditLog.builder()
                .userId(event.getUserId())
                .username(event.getUsername())
                .serviceName(event.getServiceName())
                .eventType(event.getEventType())
                .action(event.getAction())
                .status(event.getStatus())
                .description(event.getDescription())
                .requestId(event.getRequestId())
                .metadata(metadataJson)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}
