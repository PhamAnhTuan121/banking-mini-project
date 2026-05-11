CREATE DATABASE IF NOT EXISTS account_db;
USE account_db;

CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type ENUM('SAVING', 'CURRENT', 'LOAN') DEFAULT 'CURRENT',
    currency_code VARCHAR(3) NOT NULL DEFAULT 'VND',
    balance DECIMAL(15,2) DEFAULT 0,
    status ENUM('ACTIVE', 'BLOCKED', 'CLOSED','FROZEN') DEFAULT 'ACTIVE',
    version INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

INSERT INTO accounts (user_id, account_number, account_type, balance)
VALUES
    (3, '123-456-001', 'CURRENT', 5000000.00),
    (3, '123-456-002', 'SAVING', 20000000.00),
    (2, '111-222-333', 'CURRENT', 10000000.00);