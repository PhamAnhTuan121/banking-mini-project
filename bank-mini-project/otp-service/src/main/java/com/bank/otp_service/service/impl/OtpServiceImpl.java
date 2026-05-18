package com.bank.otp_service.service.impl;

import com.bank.bank_common.dto.otp.OtpType;
import com.bank.bank_common.exception.BusinessException;
import com.bank.bank_common.exception.ErrorCode;
import com.bank.otp_service.entity.OtpData;
import com.bank.otp_service.redis.OtpRedisRepository;
import com.bank.otp_service.service.OtpService;
import com.bank.otp_service.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRedisRepository otpRedisRepository;

    private long getTtl(OtpType type) {
        return switch (type) {
            case REGISTER, VERIFY_PHONE, VERIFY_OLD_PHONE, FORGOT_PASSWORD, CHANGE_PHONE -> 300;
            case TRANSFER -> 180;

        };
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return java.security.MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }

    @Override
    public void sendOtp(String identifier, OtpType type) {

        identifier = normalize(identifier);

        if (otpRedisRepository.isCooldown(identifier, type)) {
            throw new BusinessException(ErrorCode.OTP_COOLDOWN);
        }

        otpRedisRepository.clearAll(identifier, type);

        String otp = OtpGenerator.generate6Digit();

        otpRedisRepository.saveOtp(identifier, type, otp, getTtl(type));

        log.info("OTP generated: identifier={}, type={}", identifier, type);
        System.out.println("OTP: " + otp);
    }

    @Override
    public void verifyOtp(String identifier, String inputOtp, OtpType type) {

        identifier = normalize(identifier);

        if (inputOtp == null || !inputOtp.matches("\\d{6}")) {
            throw new BusinessException(ErrorCode.OTP_INVALID);
        }

        OtpData data = otpRedisRepository.getOtpData(identifier, type);

        if (data == null) {
            throw new BusinessException(ErrorCode.OTP_NOT_NULL);
        }

        if (safeEquals(data.getCode(), inputOtp)) {

            otpRedisRepository.clearAll(identifier, type);
            otpRedisRepository.resetAttempts(identifier, type); // 🔥 ADD

            return;
        }

        int attempts = otpRedisRepository.incrementAttemptsAndGet(identifier, type);

        if (attempts >= 5) {
            otpRedisRepository.setCooldown(identifier, type, 60); // lock 1 min
            otpRedisRepository.resetAttempts(identifier, type);
            throw new BusinessException(ErrorCode.TOO_MANY_ATTEMPTS);
        }

        throw new BusinessException(ErrorCode.OTP_INVALID);
    }

    private String normalize(String identifier) {
        return identifier == null ? null : identifier.trim().toLowerCase();
    }

    @Override
    public void resendOtp(String identifier, OtpType type) {

        identifier = normalize(identifier);

        if (otpRedisRepository.isCooldown(identifier, type)) {
            throw new BusinessException(ErrorCode.OTP_COOLDOWN);
        }

        otpRedisRepository.resetAttempts(identifier, type);

        otpRedisRepository.clearAll(identifier, type);

        String otp = OtpGenerator.generate6Digit();

        otpRedisRepository.saveOtp(identifier, type, otp, getTtl(type));

        otpRedisRepository.setCooldown(identifier, type, 30);

        log.info("OTP resent: identifier={}, type={}", identifier, type);
        System.out.println("Resend OTP: " + otp);
    }
}