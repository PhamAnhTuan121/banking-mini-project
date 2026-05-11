package com.bank.auth_service.redis;

import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AuthRedisRepositoryImpl implements AuthRedisRepository {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME = 1;
    private static final String LOGIN_ATTEMPTS_KEY =
            "login_attempts:";

    private static final String PHONE_CHANGE_KEY =
            "auth:phone:change:";

    @Override
    public int increaseLoginAttempts(String username) {

        String key = "login_attempts:" + username;

        Long count = redisTemplate.opsForValue().increment(key);

        // set TTL lần đầu
        if (count != null && count == 1) {
            redisTemplate.expire(key, LOCK_TIME, TimeUnit.MINUTES);
        }

        // quá số lần → block
        if (count != null && count >= MAX_ATTEMPTS) {
            throw new BusinessException(ErrorCode.TOO_MANY_ATTEMPTS);
        }

        return count == null ? 0 : count.intValue();
    }

    @Override
    public void resetLoginAttempts(String username) {
        redisTemplate.delete("login_attempts:" + username);
    }

    @Override
    public int getLoginAttempts(String username) {
        String value = redisTemplate.opsForValue().get("login_attempts:" + username);
        return value == null ? 0 : Integer.parseInt(value);
    }

    @Override
    public void savePendingPhoneChange(Long userId,
                                       String phone) {

        redisTemplate.opsForValue().set(
                PHONE_CHANGE_KEY + userId,
                phone,
                5,
                TimeUnit.MINUTES
        );
    }

    @Override
    public String getPendingPhoneChange(Long userId) {

        return redisTemplate.opsForValue()
                .get(PHONE_CHANGE_KEY + userId);
    }

    @Override
    public void deletePendingPhoneChange(Long userId) {

        redisTemplate.delete(PHONE_CHANGE_KEY + userId);
    }
}