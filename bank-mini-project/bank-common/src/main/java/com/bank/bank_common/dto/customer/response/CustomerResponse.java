package com.bank.bank_common.dto.customer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private String customerId;
    private String userId;
    private String phone;
    @JsonProperty("phone_verified")
    private boolean phoneVerified;
}