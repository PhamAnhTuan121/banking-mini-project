package com.bank.bank_common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== SYSTEM =====
    INTERNAL_ERROR(500, "SYS_500", "Internal server error"),
    SYSTEM_ERROR(500, "SYS_999", "System error"),

    // ===== ACCOUNT =====
    ACCOUNT_NOT_FOUND(404, "ACC_001", "Account not found"),
    INSUFFICIENT_BALANCE(400, "ACC_002", "Insufficient balance"),
    ACCOUNT_ALREADY_EXISTS(400, "ACC_003", "Account already exists"),
    INVALID_ACCOUNT_STATUS(400, "ACC_004", "Invalid account status"),
    INVALID_AMOUNT(400, "ACC_005", "Amount must be greater than zero"),
    SAME_ACCOUNT_TRANSFER(400, "ACC_006", "Cannot transfer to same account"),

    ACCOUNT_BLOCKED(403, "ACC_007", "Account is blocked"),
    ACCOUNT_ALREADY_BLOCKED(400, "ACC_008", "Account already blocked"),
    ACCOUNT_ALREADY_ACTIVE(400, "ACC_009", "Account already active"),
    ACCOUNT_ALREADY_CLOSED(400, "ACC_010", "Account already closed"),
    ACCOUNT_BALANCE_NOT_ZERO(400, "ACC_011", "Account balance must be zero before closing"),
    ACCOUNT_NOT_ACTIVE(400, "ACC_012", "Account not active"),

    ACCOUNT_SERVICE_UNAVAILABLE(503, "ACC_013", "Account service unavailable"),
    ACCOUNT_ALREADY_FROZEN(400, "ACC_014", "Account already frozen"),
    ACCOUNT_NOT_FROZEN(400, "ACC_015", "Account not frozen"),
    ACCOUNT_FROZEN(400, "ACC_016", "Account frozen"),
    ACCOUNT_CLOSED(400, "ACC_017", "Account closed"),



    // ===== CUSTOMER =====
    PHONE_NOT_FOUND(400, "PHONE_001", "Phone not found"),
    CUSTOMER_NOT_FOUND(400, "PHONE_002", "Customer not found"),
    CUSTOMER_ALREADY_EXISTS(400, "PHONE_003", "Customer already exists"),
    FULL_NAME_NOT_NULL(400, "NAME_004", "Full name must not be null"),

    // ===== AUTH =====
    USER_NOT_FOUND(404, "AUTH_001", "User not found"),
    INVALID_CREDENTIALS(401, "AUTH_002", "Invalid username or password"),
    PHONE_ALREADY_EXISTS(400, "AUTH_003", "Phone already exists"),
    ROLE_NOT_FOUND(404, "AUTH_004", "Role not found"),
    PHONE_ALREADY_VERIFIED(400, "AUTH_005", "Phone already verified"),
    INVALID_REFRESH_TOKEN(401, "AUTH_006", "Invalid refresh token"),
    USER_INACTIVE(403, "AUTH_007", "User is not activated"),
    USER_NOT_FOUND_WITH_PHONE(404, "AUTH_008", "User not found with phone number"),
    PHONE_ALREADY_REGISTER(400, "AUTH_009", "Phone already registered"),
    TOO_MANY_LOGIN_ATTEMPTS(429, "AUTH_010", "Too many login attempts"),
    TOO_MANY_REQUESTS(429, "AUTH_011", "Too many requests"),
    AUTH_SERVICE_UNAVAILABLE(429, "AUTH_012", "Auth service unavailable"),
    EMAIL_ALREADY_EXISTS(400, "EMAIL_001", "Email already exists"),
    USERNAME_ALREADY_EXISTS(400, "USERNAME_001", "Username already exists"),
    FULL_NAME_ALREADY_EXISTS(400, "FULLNAME_001", "Full name already exists"),
    PASSWORD_DUPLICATED(400, "PASSWORD_DUPLICATED", "Password duplicated"),
    INVALID_OLD_PASSWORD(401, "AUTH_0013", "Old password is incorrect"),
    INVALID_PASSWORD(401, "AUTH_0014", "Password is incorrect"),
    PASSWORD_MUST_BE_DIFFERENT(401, "AUTH_0015", "Password must be different"),

    // ===== OTP =====
    OTP_INVALID(400, "OTP_001", "OTP is invalid"),
    OTP_EXPIRED(400, "OTP_002", "OTP is expired"),
    TOO_MANY_ATTEMPTS(400, "OTP_003", "Too many attempts"),
    OTP_NOT_FOUND(404, "OTP_004", "No active OTP"),
    OTP_SERVICE_UNAVAILABLE(503, "OTP_005", "OTP service unavailable"),
    OTP_ALREADY_USED(400, "OTP_006", "OTP already used"),
    OTP_COOLDOWN(400, "OTP_007", "OTP Cooldown"),
    OTP_NOT_NULL(400, "OTP_008", "OTP Not null"),

    // ===== TRANSACTION =====
    TRANSACTION_NOT_FOUND(404, "TS_001", "Transaction not found"),
    INVALID_TRANSACTION_STATE(400, "TS_002", "Invalid transaction state"),
    TRANSACTION_FAILED(400, "TS_007", "Transaction failed"),
    TRANSACTION_EXPIRED(400, "TS_003", "Transaction expired"),
    UNAUTHORIZED_TRANSFER(403, "TS_004", "Unauthorized transfer"),
    USER_SERVICE_UNAVAILABLE(400, "TS_005", "User service unavailable"),
    OTP_VERIFICATION_FAILED(400, "TS_006", "OTP verification failed"),
    CANNOT_CANCEL_SUCCESS(400, "TS_007", "Can't cancel success"),
    ALREADY_REFUNDED(400, "TS_008", "Refund failed"),

    UNAUTHORIZED(401, "TS_006", "Unauthorized");

    private final int status;
    private final String code;
    private final String message;

}