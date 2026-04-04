package com.bank.bank_common.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivatedEvent {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("phone")
    private String phone;
}
