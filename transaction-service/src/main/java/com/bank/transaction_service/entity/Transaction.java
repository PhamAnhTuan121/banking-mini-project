package com.bank.transaction_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String fromAccount;
    @Pattern(regexp = "^[0-9\\-]+$", message = "Invalid account number")
    private String toAccount;
    private BigDecimal amount;
    @Column(name = "correlation_id", nullable = false)
    private String correlationId;
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
