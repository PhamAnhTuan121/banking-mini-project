DROP DATABASE IF EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

-- Bảng Role (1 role có nhiều user)
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name ENUM('ADMIN', 'EMPLOYEE', 'CUSTOMER') UNIQUE NOT NULL
);

-- Bảng User
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100),
                       role_id INT NOT NULL,  -- Mỗi user chỉ có 1 role
                       status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 🔥 Bảng Refresh Token
CREATE TABLE refresh_tokens (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token VARCHAR(255) UNIQUE NOT NULL,
                                expiry_date DATETIME NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                revoked BOOLEAN DEFAULT FALSE,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO roles (name) VALUES ('ADMIN'), ('EMPLOYEE'), ('CUSTOMER');

CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;

-- Xóa bảng cũ nếu tồn tại
DROP TABLE IF EXISTS user_profiles;

-- Bảng user_profiles: lưu thông tin cá nhân người dùng (tham chiếu logic từ auth_db.users)
CREATE TABLE user_profiles (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL, -- Tham chiếu logic từ auth_db.users.id
                               national_id VARCHAR(20), -- CMND / CCCD
                               phone_number VARCHAR(15),
                               date_of_birth DATE,
                               address VARCHAR(255),
                               gender ENUM('MALE', 'FEMALE', 'OTHER'),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu
INSERT INTO user_profiles (user_id, national_id, phone_number, date_of_birth, address, gender)
VALUES
    (1, '012345678', '0901234567', '1990-01-01', 'Hanoi, Vietnam', 'MALE'),
    (2, '098765432', '0912345678', '1995-05-05', 'Da Nang, Vietnam', 'FEMALE'),
    (3, '112233445', '0934567890', '2000-08-15', 'Ho Chi Minh City, Vietnam', 'MALE');
CREATE DATABASE IF NOT EXISTS account_db;
USE account_db;

-- Xóa bảng cũ nếu tồn tại
DROP TABLE IF EXISTS accounts;

-- Bảng accounts: quản lý tài khoản ngân hàng
CREATE TABLE accounts (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL, -- Tham chiếu logic đến auth_db.users
                          account_number VARCHAR(20) UNIQUE NOT NULL,
                          account_type ENUM('SAVING', 'CURRENT', 'LOAN') DEFAULT 'CURRENT',
                          currency_code VARCHAR(3) NOT NULL DEFAULT 'VND',
                          balance DECIMAL(15,2) DEFAULT 0,
                          status ENUM('ACTIVE', 'BLOCKED', 'CLOSED') DEFAULT 'ACTIVE',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu
INSERT INTO accounts (user_id, account_number, account_type, balance)
VALUES
    (3, '123-456-001', 'CURRENT', 5000000.00),
    (3, '123-456-002', 'SAVING', 20000000.00),
    (2, '111-222-333', 'CURRENT', 10000000.00);
CREATE DATABASE IF NOT EXISTS transaction_db;
USE transaction_db;

-- Xóa bảng cũ nếu tồn tại
DROP TABLE IF EXISTS transactions;

-- Bảng transactions: quản lý giao dịch
CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              correlation_id VARCHAR(50) NOT NULL,
                              transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER') NOT NULL,
                              from_account VARCHAR(20),
                              to_account VARCHAR(20),
                              amount DECIMAL(15,2) NOT NULL,
                              fee DECIMAL(15,2) DEFAULT 0,
                              description VARCHAR(255),
                              status ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu
INSERT INTO transactions (correlation_id, transaction_type, from_account, to_account, amount, fee, description, status)
VALUES
    ('TXN001', 'DEPOSIT', NULL, '123-456-001', 1000000.00, 0, 'Nạp tiền', 'SUCCESS'),
    ('TXN002', 'TRANSFER', '123-456-001', '111-222-333', 500000.00, 5000.00, 'Chuyển khoản cho nhân viên', 'SUCCESS');
