package com.bank.otp_service.redis;

import com.bank.bank_common.dto.otp.OtpType;
import com.bank.otp_service.entity.OtpData;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRedisRepository {

    void saveOtp(String identifier, OtpType type, String otp, long ttl);

    OtpData getOtpData(String identifier, OtpType type);

    void resetAttempts(String identifier, OtpType type);

    void clearAll(String identifier, OtpType type);

    boolean isCooldown(String identifier, OtpType type);

    void setCooldown(String identifier, OtpType type, long seconds);

    int incrementAttemptsAndGet(String identifier, OtpType type);

}
