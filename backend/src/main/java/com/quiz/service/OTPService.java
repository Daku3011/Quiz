package com.quiz.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

// This service handles the security for our quiz sessions by generating  and "sending" One-Time Passwords (OTPs) to the faculty.
@Service
public class OTPService {
    private final SecureRandom rnd = new SecureRandom();
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final com.quiz.repository.OtpRepository otpRepo;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OTPService.class);

    public OTPService(org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            com.quiz.repository.OtpRepository otpRepo) {
        this.passwordEncoder = passwordEncoder;
        this.otpRepo = otpRepo;
    }

    public static class OtpResult {
        public final String plainCode;
        public final com.quiz.model.Otp otpEntity;

        public OtpResult(String plainCode, com.quiz.model.Otp otpEntity) {
            this.plainCode = plainCode;
            this.otpEntity = otpEntity;
        }
    }

    public OtpResult generateSecureOtp() {
        int code = 100000 + rnd.nextInt(900000);
        String plainCode = String.valueOf(code);
        String hashed = passwordEncoder.encode(plainCode);

        // 24 hours expiry
        java.time.Instant expiry = java.time.Instant.now().plus(24, java.time.temporal.ChronoUnit.HOURS);

        com.quiz.model.Otp otpEntity = new com.quiz.model.Otp(hashed, expiry);
        otpEntity.setCode(plainCode); // Storing plain code for history display
        // We don't save it yet; it will be saved via cascade from Session

        return new OtpResult(plainCode, otpEntity);
    }

    public boolean validateOtp(com.quiz.model.Session session, String inputCode) {
        if (session == null || session.getOtpDetails() == null)
            return false;

        com.quiz.model.Otp otp = session.getOtpDetails();

        if (otp.isBlocked()) {
            log.warn("OTP validation failed: Blocked due to too many attempts. SessionID={}", session.getId());
            return false;
        }

        if (otp.isExpired()) {
            log.warn("OTP validation failed: Expired. SessionID={}", session.getId());
            return false;
        }

        boolean match = passwordEncoder.matches(inputCode, otp.getCodeHash());

        if (!match) {
            otp.incrementAttempts();
            otpRepo.save(otp);
            log.warn("OTP validation failed: Invalid code. Attempts={}. SessionID={}", otp.getAttempts(),
                    session.getId());
            return false;
        }

        return true;
    }

    public void sendOtpTo(String destination, String otp) {
        // placeholder - in production would send SMS/Email
        log.info("[OTP] Sent to {}: {}", destination, otp);
    }
}
