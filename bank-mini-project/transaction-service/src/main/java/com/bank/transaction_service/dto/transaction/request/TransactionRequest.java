package com.bank.transaction_service.dto.transaction.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String description;
}