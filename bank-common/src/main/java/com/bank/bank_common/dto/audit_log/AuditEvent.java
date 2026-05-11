package com.bank.bank_common.dto.audit_log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditEvent {

    private Long userId;
    private String username;
    private String serviceName;

    private String eventType;
    private String action;
    private String status;

    private String description;

    private String requestId;

    private Map<String, Object> metadata;

}