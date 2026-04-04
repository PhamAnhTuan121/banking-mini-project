package com.bank.transaction_service.dto.transaction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("balance")
    private BigDecimal balance;

    private String email;

    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
