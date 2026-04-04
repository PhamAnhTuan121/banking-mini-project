package com.bank.account_service.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountCreateRequest {
    private Long userId;

    @JsonCreator
    public AccountCreateRequest(
            @JsonProperty("userId") Long userId) {
        this.userId = userId;
    }

}
