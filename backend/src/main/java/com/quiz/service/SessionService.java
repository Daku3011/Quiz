package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.model.Session;
import com.quiz.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage the lifecycle of quiz sessions.
 * Handles creation, retrieval, and OTP validation.
 */
@Service
public class SessionService {
    private final SessionRepository sessionRepo;
    private final OTPService otpService;
    // simple in-memory mapping sessionId -> questions
    private final Map<Long, List<Question>> sessionQuestions = new HashMap<>();

    public SessionService(SessionRepository sessionRepo, OTPService otpService) {
        this.sessionRepo = sessionRepo;
        this.otpService = otpService;
    }

    /**
     * Creates a new session and stores associated questions in memory.
     * 
     * @param title      Quiz title.
     * @param otpDetails Secure OTP Entity.
     * @param questions  List of generated questions.
     * @return Persisted session entity.
     */
    public Session createSession(String title, com.quiz.model.Otp otpDetails, List<Question> questions) {
        Session s = new Session();
        s.setTitle(title);
        s.setOtpDetails(otpDetails);
        s.setActive(true);
        Session saved = sessionRepo.save(s);
        sessionQuestions.put(saved.getId(), questions);
        return saved;
    }

    public List<Question> getQuestionsFor(Long sessionId) {
        return sessionQuestions.getOrDefault(sessionId, List.of());
    }

    public boolean validateOtp(Long sessionId, String otp) {
        Session session = sessionRepo.findById(sessionId).orElse(null);
        if (session == null || !session.isActive())
            return false;

        return otpService.validateOtp(session, otp);
    }

    public Session getSession(Long sessionId) {
        return sessionRepo.findById(sessionId).orElse(null);
    }

    public List<Session> getActiveSessions() {
        return sessionRepo.findByActiveTrue();
    }

    public void stopSession(Long sessionId) {
        sessionRepo.findById(sessionId).ifPresent(s -> {
            s.setActive(false);
            s.setEndTime(java.time.Instant.now());
            sessionRepo.save(s);
        });
    }
}
