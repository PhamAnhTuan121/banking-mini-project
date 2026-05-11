package com.bank.bank_common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusEvent {
    private String eventId;
    private Long userId;
    private String eventType;
    private LocalDateTime timestamp;
}