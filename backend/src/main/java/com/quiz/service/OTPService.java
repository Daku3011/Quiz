package com.quiz.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OTPService {
    private final SecureRandom rnd = new SecureRandom();
    public String generateOTP() {
        int code = 100000 + rnd.nextInt(900000);
        return String.valueOf(code);
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OTPService.class);
    public void sendOtpTo(String destination, String otp) {
        // placeholder - print to console.
        log.info("[OTP] send to {} OTP={}", destination, otp);
    }
}
