package com.bank.otp_service.util;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@RequiredArgsConstructor
public class OtpGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate6Digit(){
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
