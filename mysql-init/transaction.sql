CREATE DATABASE IF NOT EXISTS transaction_db;
USE transaction_db;

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            correlation_id VARCHAR(50) NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER') NOT NULL,
    from_account VARCHAR(20),
    to_account VARCHAR(20),
    amount DECIMAL(15,2) NOT NULL,
    fee DECIMAL(15,2) DEFAULT 0,
    description VARCHAR(255),
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'EXPIRED', 'CANCELLED','REFUNDED') DEFAULT 'PENDING',
    expired_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE INDEX idx_correlation_id ON transactions(correlation_id);
CREATE INDEX idx_from_account ON transactions(from_account);
CREATE INDEX idx_to_account ON transactions(to_account);