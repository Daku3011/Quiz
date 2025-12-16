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

@RestController
@RequestMapping("/api/session")
public class SessionController {
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

    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody com.quiz.dto.SessionDTOs.StartSessionRequest body) {
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
            return q;
        }).collect(Collectors.toList());

        List<Question> saved = questionRepo.saveAll(questions);

        String otp = otpService.generateOTP();
        Session s = sessionService.createSession(title, otp, saved);

        // Handle Start/End Time
        try {
            java.time.Instant start = body.getStartTime() != null
                    ? java.time.Instant.parse(body.getStartTime())
                    : java.time.Instant.now();
            s.setStartTime(start);

            if (body.getDurationMinutes() != null && body.getDurationMinutes() > 0) {
                s.setEndTime(start.plus(java.time.Duration.ofMinutes(body.getDurationMinutes())));
            }
            if (body.getNumberOfSets() != null && body.getNumberOfSets() > 1) {
                s.setNumberOfSets(body.getNumberOfSets());
            }
            sessionRepo.save(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        otpService.sendOtpTo("faculty-console", otp);
        return ResponseEntity.ok(Map.of("sessionId", s.getId(), "otp", otp));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinSession(@RequestBody com.quiz.dto.SessionDTOs.JoinSessionRequest body) {
        Long sessionId = body.getSessionId();
        if (sessionId == null)
            return ResponseEntity.badRequest().body(Map.of("error", "invalid sessionId"));
        String otp = body.getOtp();
        boolean ok = sessionService.validateOtp(sessionId, otp);
        if (!ok)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid otp"));

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

        // Partition Logic
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
}
