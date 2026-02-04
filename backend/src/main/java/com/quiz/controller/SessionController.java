package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.model.Session;
import com.quiz.repository.QuestionRepository;
import com.quiz.service.OTPService;
import com.quiz.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.quiz.repository.SessionRepository;

// This is one of the busiest controllers. It manages the lifecycle of a quiz session—
// from starting it and generating an OTP to letting students join and then stopping it.
@RestController
@RequestMapping("/api/session")
public class SessionController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;
    private final OTPService otpService;
    private final QuestionRepository questionRepo;
    private final SessionRepository sessionRepo;

    public SessionController(SessionService sessionService, OTPService otpService, QuestionRepository questionRepo,
            SessionRepository sessionRepo) {
        this.sessionService = sessionService;
        this.otpService = otpService;
        this.questionRepo = questionRepo;
        this.sessionRepo = sessionRepo;
    }

    // When a faculty member clicks "Start Quiz", this method sets everything up.
    // It saves the questions, creates the session, and generates a fresh OTP.
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody com.quiz.dto.SessionDTOs.StartSessionRequest body) {
        logger.info("Request to start session: Title='{}'", body.getTitle());

        String title = body.getTitle() == null ? "Quiz" : body.getTitle();
        List<com.quiz.dto.SessionDTOs.QuestionRequest> qlist = body.getQuestions() == null ? List.of()
                : body.getQuestions();

        List<Question> questions = qlist.stream().map(m -> {
            Question q = new Question();
            q.setText(m.getText());
            q.setOptionA(m.getOptionA() == null ? "A" : m.getOptionA());
            q.setOptionB(m.getOptionB() == null ? "B" : m.getOptionB());
            q.setOptionC(m.getOptionC() == null ? "C" : m.getOptionC());
            q.setOptionD(m.getOptionD() == null ? "D" : m.getOptionD());
            q.setCorrect(m.getCorrect() == null ? "A" : m.getCorrect());
            q.setExplanation(m.getExplanation());
            q.setChapter(m.getChapter());
            q.setCourseOutcome(m.getCourseOutcome());
            return q;
        }).collect(Collectors.toList());

        List<Question> saved = questionRepo.saveAll(questions);

        // Generate secure OTP
        com.quiz.service.OTPService.OtpResult otpResult = otpService.generateSecureOtp();
        Session s = sessionService.createSession(title, otpResult.otpEntity, saved);

        // Handle Start/End Time
        try {
            String t = body.getStartTime();
            java.time.Instant start;
            if (t == null) {
                start = java.time.Instant.now();
            } else {
                try {
                    start = java.time.Instant.parse(t);
                } catch (java.time.format.DateTimeParseException e) {
                    // Fallback for LocalDateTime (e.g. "2023-12-15T10:00")
                    start = java.time.LocalDateTime.parse(t).atZone(java.time.ZoneId.systemDefault()).toInstant();
                }
            }
            s.setStartTime(start);

            if (body.getDurationMinutes() != null && body.getDurationMinutes() > 0) {
                s.setEndTime(start.plus(java.time.Duration.ofMinutes(body.getDurationMinutes())));
            }
            if (body.getNumberOfSets() != null && body.getNumberOfSets() > 1) {
                s.setNumberOfSets(body.getNumberOfSets());
            }
            sessionRepo.save(s);
            logger.info("Session started: ID={}, Title='{}', Questions={}", s.getId(), s.getTitle(), saved.size());
        } catch (Exception e) {
            logger.error("Error setting session times", e);
        }

        otpService.sendOtpTo("faculty-console", otpResult.plainCode);
        return ResponseEntity.ok(Map.of("sessionId", s.getId(), "otp", otpResult.plainCode));
    }

    // This is what students hit when they try to join a session.
    // We check the OTP and make sure the exam is actually running (not too early,
    // not too late).
    @PostMapping("/join")
    public ResponseEntity<?> joinSession(@RequestBody com.quiz.dto.SessionDTOs.JoinSessionRequest body) {
        Long sessionId = body.getSessionId();
        if (sessionId == null)
            return ResponseEntity.badRequest().body(Map.of("error", "invalid sessionId"));

        String otp = body.getOtp();
        boolean ok = sessionService.validateOtp(sessionId, otp);
        if (!ok) {
            logger.warn("Failed join attempt: SessionID={}, OTP={}", sessionId, otp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid otp"));
        }

        // Time Validation
        Session s = sessionService.getSession(sessionId);
        if (s != null) {
            java.time.Instant now = java.time.Instant.now();
            if (s.getStartTime() != null && now.isBefore(s.getStartTime())) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Exam has not started yet. Starts at: " + s.getStartTime()));
            }
            if (s.getEndTime() != null && now.isAfter(s.getEndTime())) {
                return ResponseEntity.status(403).body(Map.of("error", "Exam has ended."));
            }
        }

        logger.info("Student joined session: SessionID={}", sessionId);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable("id") Long id,
            @RequestParam(value = "studentId", required = false) Long studentId) {
        List<Question> allQuestions = sessionService.getQuestionsFor(id);
        Session s = sessionService.getSession(id);
        if (s != null) {
            java.time.Instant now = java.time.Instant.now();
            if (s.getStartTime() != null && now.isBefore(s.getStartTime())) {
                return ResponseEntity.status(403).body(Map.of("error", "Exam not started"));
            }
        }
        if (allQuestions.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "no questions or invalid session"));

        int sets = s != null ? s.getNumberOfSets() : 1;
        if (sets < 1)
            sets = 1;

        // This is the logic for splitting questions into different sets.
        // It helps prevent cheating by giving different students different questions
        // (or a different order).
        List<Question> assignedQuestions;
        if (studentId != null && sets > 1) {
            long setIndex = studentId % sets; // 0 to sets-1
            // Filter questions: index % sets == setIndex
            // We need efficient filtering.
            assignedQuestions = new java.util.ArrayList<>();
            for (int i = 0; i < allQuestions.size(); i++) {
                if (i % sets == setIndex) {
                    assignedQuestions.add(allQuestions.get(i));
                }
            }
        } else {
            assignedQuestions = new java.util.ArrayList<>(allQuestions);
        }

        // Shuffle Logic
        if (studentId != null) {
            java.util.Collections.shuffle(assignedQuestions, new java.util.Random(studentId));
        }

        return ResponseEntity.ok(assignedQuestions);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getSessionStatus(@PathVariable("id") Long id) {
        Session s = sessionService.getSession(id);
        if (s == null)
            return ResponseEntity.notFound().build();

        java.time.Instant now = java.time.Instant.now();
        String status = "WAITING";

        if (s.getStartTime() != null && now.isAfter(s.getStartTime())) {
            status = "ACTIVE";
        }

        if (s.getEndTime() != null && now.isAfter(s.getEndTime())) {
            status = "ENDED";
        }

        if (!s.isActive()) {
            status = "ENDED";
        }

        return ResponseEntity
                .ok(Map.of("status", status, "startTime", s.getStartTime() != null ? s.getStartTime().toString() : ""));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopSession(@PathVariable("id") Long id) {
        sessionService.stopSession(id);
        logger.info("Session stopped manually: ID={}", id);
        return ResponseEntity.ok(Map.of("status", "stopped"));
    }
}
