package com.bank.bank_common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSuccessEvent {
    private String correlationId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private Long senderUserId;
    private Long receiverUserId;
}
