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

    @Override
    public int getLoginAttempts(String username) {
        String value = redisTemplate.opsForValue().get(loginKey(username));
        return value == null ? 0 : Integer.parseInt(value);
    }

    @Override
    public void resetLoginAttempts(String username) {
        redisTemplate.delete(loginKey(username));
    }

    @Override
    public void checkLoginRateLimit(String username) {

        String key = rateLimitKey(username);

        Long count = redisTemplate.opsForValue().increment(key);

        if(count != null && count == 1){
            redisTemplate.expire(key , 60 , TimeUnit.SECONDS);
        }

        if(count != null && count > 10){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }
    }

    private String loginKey(String username){
        return "login_attempts:" + username;
    }

    private String rateLimitKey(String username){
        return "rate_limit:login:" + username;
    }
}
