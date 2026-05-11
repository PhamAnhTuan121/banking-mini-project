package com.bank.customer_service.repository;

import com.bank.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Customer> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
