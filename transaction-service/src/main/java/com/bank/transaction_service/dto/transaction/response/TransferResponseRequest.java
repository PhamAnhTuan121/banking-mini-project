package com.bank.transaction_service.dto.transaction.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResponseRequest {
    private String correlationId;
    private String status;
    private String message;
}
