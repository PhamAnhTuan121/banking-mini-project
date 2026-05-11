package com.bank.account_service.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountUpdateBalanceRequest {
    private BigDecimal amount;
}