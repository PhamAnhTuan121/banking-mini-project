package com.bank.auth_service.redis;

public interface AuthRedisRepository {

    void checkLoginRateLimit(String username);

    int getLoginAttempts(String username);

    void resetLoginAttempts(String username);
}
