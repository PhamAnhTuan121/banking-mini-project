package com.bank.bank_common.dto.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSuccessEvent {

    private String correlationId;

    private String fromAccount;

    private String toAccount;

    private BigDecimal amount;

    private String description;

    private String receiverName;

    private String senderName;

    private String bankName;

    private LocalDateTime transactionTime;

    private Long senderUserId;

    private Long receiverUserId;
}