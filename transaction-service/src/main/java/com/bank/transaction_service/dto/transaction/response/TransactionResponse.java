package com.bank.transaction_service.dto.transaction.response;

import com.bank.transaction_service.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String correlationId;
    private TransactionType type;
    private BigDecimal amount;
    private String fromAccount;
    private String toAccount;
    private String status;
    private String direction;
    private String description;
    private String message;
    private LocalDateTime timestamp;
}
