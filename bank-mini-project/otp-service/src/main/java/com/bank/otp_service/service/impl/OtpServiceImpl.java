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

    private static final int MAX_ATTEMPTS = 5;

    private long getTtl(OtpType type) {
        return switch (type) {
            case REGISTER, VERIFY_PHONE -> 300;
            case TRANSFER -> 180;
        };
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return java.security.MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }

    @Override
    public void sendOtp(String identifier, OtpType type) {

        if (otpRedisRepository.isCooldown(identifier, type)) {
            throw new BusinessException(ErrorCode.OTP_COOLDOWN);
        }

        String otp = OtpGenerator.generate6Digit();

        otpRedisRepository.saveOtp(identifier, type, otp, getTtl(type));
        otpRedisRepository.resetAttempts(identifier, type);
        otpRedisRepository.setCooldown(identifier, type, 30);

        log.info("OTP generated for identifier={}, type={}", identifier, type);

        System.out.println("OTP: " + otp);
    }

    @Override
    public void verifyOtp(String identifier, String inputOtp, OtpType type) {

        if (inputOtp == null || !inputOtp.matches("\\d{6}")) {
            throw new BusinessException(ErrorCode.OTP_INVALID);
        }

        OtpData data = otpRedisRepository.getOtpData(identifier, type);

        if (data == null) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED);
        }

        if (data.isUsed()) {
            throw new BusinessException(ErrorCode.OTP_ALREADY_USED);
        }

        if (!safeEquals(data.getCode(), inputOtp)) {

            otpRedisRepository.incrementAttempts(identifier, type);

            int attempts = otpRedisRepository.getAttempts(identifier, type);

            if (attempts >= MAX_ATTEMPTS) {
                otpRedisRepository.clearAll(identifier, type);
                otpRedisRepository.setCooldown(identifier, type, 300);
                throw new BusinessException(ErrorCode.OTP_BLOCKED);
            }

            throw new BusinessException(ErrorCode.OTP_INVALID);
        }

        data.setUsed(true);
        otpRedisRepository.updateOtpKeepTtl(identifier, type, data);
        otpRedisRepository.resetAttempts(identifier, type);

        log.info("OTP verified success: identifier={}, type={}", identifier, type);
    }

    @Override
    public void resendOtp(String identifier, OtpType type) {

        if (otpRedisRepository.isCooldown(identifier, type)) {
            throw new BusinessException(ErrorCode.OTP_COOLDOWN);
        }

        int attempts = otpRedisRepository.getAttempts(identifier, type);

        if (attempts >= MAX_ATTEMPTS) {
            throw new BusinessException(ErrorCode.OTP_BLOCKED);
        }

        String otp = OtpGenerator.generate6Digit();

        otpRedisRepository.saveOtp(identifier, type, otp, getTtl(type));
        otpRedisRepository.setCooldown(identifier, type, 30);

        log.info("OTP resent for identifier={}, type={}", identifier, type);
    }
}