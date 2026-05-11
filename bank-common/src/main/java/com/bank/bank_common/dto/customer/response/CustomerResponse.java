package com.bank.bank_common.dto.customer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {

    private String id;
    private String userId;
//    private String fullName;
//    private String phone;
    private String address;
    private BigDecimal dailyLimit;

}