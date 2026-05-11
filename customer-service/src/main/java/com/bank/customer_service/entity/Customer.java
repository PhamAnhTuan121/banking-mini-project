package com.bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

//    @Column(name = "full_name", nullable = false)
//    private String fullName;

//    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status" , nullable = false)
    private KycStatus kycStatus;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "daily_limit", nullable = false)
    private BigDecimal dailyLimit;

    @Column(name = "used_today", nullable = false)
    private BigDecimal usedToday;

    @Column(name = "last_reset_date", nullable = false)
    private LocalDate lastResetDate;

}
