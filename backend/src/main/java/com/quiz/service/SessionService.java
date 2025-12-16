package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.model.Session;
import com.quiz.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SessionService {
    private final SessionRepository sessionRepo;
    // simple in-memory mapping sessionId -> questions
    private final Map<Long, List<Question>> sessionQuestions = new HashMap<>();

    public SessionService(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    public Session createSession(String title, String otp, List<Question> questions) {
        Session s = new Session();
        s.setTitle(title);
        s.setOtp(otp);
        s.setActive(true);
        Session saved = sessionRepo.save(s);
        sessionQuestions.put(saved.getId(), questions);
        return saved;
    }

    public List<Question> getQuestionsFor(Long sessionId) {
        return sessionQuestions.getOrDefault(sessionId, List.of());
    }

    public boolean validateOtp(Long sessionId, String otp) {
        return sessionRepo.findById(sessionId)
                .map(s -> s.isActive() && s.getOtp().equals(otp))
                .orElse(false);
    }

    public Session getSession(Long sessionId) {
        return sessionRepo.findById(sessionId).orElse(null);
    }
}
