package com.bank.transaction_service.dto.transaction.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private String toAccount;
    private BigDecimal amount;
    private String description;
}
