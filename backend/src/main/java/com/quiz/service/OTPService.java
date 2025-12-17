package com.quiz.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service for generating and distributing One-Time Passwords (OTPs).
 * Used for securing session access.
 */
@Service
public class OTPService {
    private final SecureRandom rnd = new SecureRandom();

    /**
     * Generates a 6-digit numeric OTP.
     * 
     * @return 6-digit OTP string.
     */
    public String generateOTP() {
        int code = 100000 + rnd.nextInt(900000);
        return String.valueOf(code);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OTPService.class);

    /**
     * Sends the OTP to the specified destination (simulated).
     * 
     * @param destination Target user/console.
     * @param otp         The OTP code.
     */
    public void sendOtpTo(String destination, String otp) {
        // placeholder - in production would send SMS/Email
        log.info("[OTP] Sent to {}: {}", destination, otp);
    }
}
