package com.bank.bank_common.dto.customer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserProfileResponse {

    private Long userId;
    private String username;
    private String phone;

    private String fullName;
    private String address;

    @JsonProperty("account_number")
    private String accountNumber;
    private BigDecimal balance;
}