package com.bank.account_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long id;
    private Long userId;
    private String accountNumber;
    private String accountType;
    private String currencyCode;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}
