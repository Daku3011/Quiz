package com.quiz.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

// This service handles the security for our quiz sessions by generating 
// and "sending" One-Time Passwords (OTPs) to the faculty.
@Service
public class OTPService {
    private final SecureRandom rnd = new SecureRandom();

    // We generate a simple 6-digit number to use as an OTP.
    public String generateOTP() {
        int code = 100000 + rnd.nextInt(900000);
        return String.valueOf(code);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OTPService.class);

    // This is where we "send" the OTP. Right now it just logs it to the console,
    // but in a real app, this is where you'd hook up an email or SMS service.
    public void sendOtpTo(String destination, String otp) {
        // placeholder - in production would send SMS/Email
        log.info("[OTP] Sent to {}: {}", destination, otp);
    }
}
