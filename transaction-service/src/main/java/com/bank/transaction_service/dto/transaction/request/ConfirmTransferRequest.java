package com.bank.transaction_service.dto.transaction.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmTransferRequest {
    private String identifier;
    private String otp;
}
