CREATE DATABASE IF NOT EXISTS audit_log_db;
USE audit_log_db;

CREATE TABLE IF NOT EXISTS audit_logs (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          user_id BIGINT,
                                          username VARCHAR(100),
    service_name VARCHAR(50),
    event_type VARCHAR(100),
    action VARCHAR(50),
    status VARCHAR(20),
    description TEXT,
    request_id VARCHAR(100),
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_request_id ON audit_logs(request_id);
CREATE INDEX idx_user_id ON audit_logs(user_id);