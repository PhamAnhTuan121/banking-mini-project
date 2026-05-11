package com.bank.auth_service.redis;

public interface AuthRedisRepository {

    int getLoginAttempts(String username);

    void resetLoginAttempts(String username);

    int increaseLoginAttempts(String username);

    // ================= PHONE CHANGE =================

    void savePendingPhoneChange(Long userId, String phone);

    String getPendingPhoneChange(Long userId);

    void deletePendingPhoneChange(Long userId);
}
