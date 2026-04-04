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
        try {
            OtpData data = OtpData.builder()
                    .code(otp)
                    .used(false)
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

            if (json == null) return null;

            return objectMapper.readValue(json, OtpData.class);

        } catch (Exception e) {
            log.error("Redis get OTP failed", e);
            throw new BusinessException(ErrorCode.OTP_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void updateOtpKeepTtl(String identifier, OtpType type, OtpData data) {
        String key = otpKey(identifier, type);

        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            if (ttl == null || ttl <= 0) {
                return;
            }

            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(data),
                    ttl,
                    TimeUnit.SECONDS
            );

        } catch (Exception e) {
            log.error("Redis update OTP failed", e);
            throw new BusinessException(ErrorCode.OTP_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void deleteOtp(String identifier, OtpType type) {
        try {
            redisTemplate.delete(otpKey(identifier, type));
        } catch (Exception e) {
            log.error("Redis delete OTP failed", e);
        }
    }

    // ================= ATTEMPT =================

    @Override
    public void incrementAttempts(String identifier, OtpType type) {
        String key = attemptKey(identifier, type);

        try {
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, ATTEMPT_TTL, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis increment attempt failed", e);
        }
    }

    @Override
    public int getAttempts(String identifier, OtpType type) {
        try {
            String value = redisTemplate.opsForValue().get(attemptKey(identifier, type));
            return value == null ? 0 : Integer.parseInt(value);
        } catch (Exception e) {
            log.error("Redis get attempt failed", e);
            return 0;
        }
    }

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
                    "1",
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
}