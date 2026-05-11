package com.bank.bank_common.dto.customer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateInternalRequest {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("fullName")
    private String fullName;
}