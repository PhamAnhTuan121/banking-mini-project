package com.bank.transaction_service.dto.transaction.response;

import com.bank.transaction_service.entity.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferResponseConfirm {
    private String correlationId;

    private String transactionType; // TRANSFER

    private String fromAccount;

    private String toAccount;

    private BigDecimal amount;

    private TransactionStatus status;

    private String description;

    private String message;
}
