package com.bank.otp_service.redis;

import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.otp_service.entity.OtpData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OtpRedisRepositoryImpl implements OtpRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long ATTEMPT_TTL = 5; // minutes

    private String otpKey(String identifier, OtpType type) {
        return "otp:" + type + ":" + identifier;
    }

    private String attemptKey(String identifier, OtpType type) {
        return "otp_attempt:" + type + ":" + identifier;
    }

    private String cooldownKey(String identifier, OtpType type) {
        return "otp_cooldown:" + type + ":" + identifier;
    }

    // ================= OTP =================

    @Override
    public void saveOtp(String identifier, OtpType type, String otp, long ttl) {
        identifier = identifier.trim().toLowerCase();
        try {
            OtpData data = OtpData.builder()
                    .code(otp)
                    .build();

            redisTemplate.opsForValue().set(
                    otpKey(identifier, type),
                    objectMapper.writeValueAsString(data),
                    ttl,
                    TimeUnit.SECONDS
            );

        } catch (Exception e) {
            log.error("Redis save OTP failed", e);
            throw new BusinessException(ErrorCode.OTP_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public OtpData getOtpData(String identifier, OtpType type) {
        String key = otpKey(identifier, type);

        try {
            String json = redisTemplate.opsForValue().get(key);
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            log.info("GET OTP key={}, ttl={}, json={}", key, ttl, json);

            if (json == null || ttl == null || ttl <= 0) {
                return null;
            }

            return objectMapper.readValue(json, OtpData.class);

        } catch (Exception e) {
            log.error("Redis get OTP failed", e);
            throw new BusinessException(ErrorCode.OTP_SERVICE_UNAVAILABLE);
        }
    }
    // ================= ATTEMPT =================

    @Override
    public void resetAttempts(String identifier, OtpType type) {
        redisTemplate.delete(attemptKey(identifier, type));
    }

    // ================= COOLDOWN =================

    @Override
    public boolean isCooldown(String identifier, OtpType type) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey(identifier, type)));
        } catch (Exception e) {
            log.error("Redis cooldown check failed", e);
            return false;
        }
    }

    @Override
    public void setCooldown(String identifier, OtpType type, long seconds) {
        try {
            redisTemplate.opsForValue().set(
                    cooldownKey(identifier, type),
                    "3",
                    seconds,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("Redis set cooldown failed", e);
        }
    }

    @Override
    public void clearAll(String identifier, OtpType type) {
        redisTemplate.delete(otpKey(identifier, type));
        redisTemplate.delete(attemptKey(identifier, type));
        redisTemplate.delete(cooldownKey(identifier, type));
    }

    @Override
    public int incrementAttemptsAndGet(String identifier, OtpType type) {
        String key = attemptKey(identifier, type);

        try {
            Long value = redisTemplate.opsForValue().increment(key);

            if (value != null && value == 1) {
                redisTemplate.expire(key, ATTEMPT_TTL, TimeUnit.MINUTES);
            }

            return value == null ? 0 : value.intValue();

        } catch (Exception e) {
            log.error("Redis increment attempt failed", e);
            return 0;
        }
    }
}