package com.bank.transaction_service.repository;

import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.entity.TransactionStatus;
import com.bank.transaction_service.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountOrToAccount(String from, String to);

    Optional<Transaction> findByCorrelationId(String correlationId);


    List<Transaction> findByStatusAndExpiredAtBefore(
            TransactionStatus status,
            LocalDateTime time
    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE (t.fromAccount = :account OR t.toAccount = :account)
    AND (:type IS NULL OR t.transactionType = :type)
    AND (:status IS NULL OR t.status = :status)
    AND (:fromDate IS NULL OR t.createdAt >= :fromDate)
    AND (:toDate IS NULL OR t.createdAt <= :toDate)
""")
    Page<Transaction> searchHistory(
            @Param("account") String account,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    long countByStatus(TransactionStatus status);

    @Query("""
    SELECT COUNT(t)
    FROM Transaction t
    WHERE DATE(t.createdAt) = CURRENT_DATE
""")
    long countToday();

    @Query("""
    SELECT SUM(t.amount)
    FROM Transaction t
""")
    Double sumAmount();

    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);
}
