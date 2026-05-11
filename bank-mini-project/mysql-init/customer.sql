CREATE DATABASE IF NOT EXISTS customer_db;
USE customer_db;

CREATE TABLE IF NOT EXISTS customer (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id BIGINT NOT NULL UNIQUE,
                                        address VARCHAR(255),
    full_name VARCHAR(100),
    phone VARCHAR(15),
    kyc_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    status ENUM('ACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    daily_limit DECIMAL(18,2) DEFAULT 50000000.00,
    used_today DECIMAL(18,2) DEFAULT 0.00,
    last_reset_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );