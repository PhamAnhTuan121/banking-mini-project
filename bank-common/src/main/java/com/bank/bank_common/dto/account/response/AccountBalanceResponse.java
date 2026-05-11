package com.bank.bank_common.dto.account.response;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountBalanceResponse {

    private String accountNumber;

    private BigDecimal balance;

    private String currency;
}