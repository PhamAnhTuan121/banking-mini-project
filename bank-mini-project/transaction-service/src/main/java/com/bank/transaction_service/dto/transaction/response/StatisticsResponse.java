package com.bank.transaction_service.dto.transaction.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsResponse {
    private long totalTransactions;
    private long failedTransactions;
    private long todayTransactions;
    private Double totalAmount;
}