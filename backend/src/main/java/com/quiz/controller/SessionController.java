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

@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;
    private final OTPService otpService;
    private final QuestionRepository questionRepo;

    public SessionController(SessionService sessionService, OTPService otpService, QuestionRepository questionRepo) {
        this.sessionService = sessionService; this.otpService = otpService; this.questionRepo = questionRepo;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody com.quiz.dto.SessionDTOs.StartSessionRequest body) {
        String title = body.getTitle() == null ? "Quiz" : body.getTitle();
        List<com.quiz.dto.SessionDTOs.QuestionRequest> qlist = body.getQuestions() == null ? List.of() : body.getQuestions();
        
        List<Question> questions = qlist.stream().map(m -> {
            Question q = new Question();
            q.setText(m.getText());
            q.setOptionA(m.getOptionA() == null ? "A" : m.getOptionA());
            q.setOptionB(m.getOptionB() == null ? "B" : m.getOptionB());
            q.setOptionC(m.getOptionC() == null ? "C" : m.getOptionC());
            q.setOptionD(m.getOptionD() == null ? "D" : m.getOptionD());
            q.setCorrect(m.getCorrect() == null ? "A" : m.getCorrect());
            return q;
        }).collect(Collectors.toList());

        List<Question> saved = questionRepo.saveAll(questions);

        String otp = otpService.generateOTP();
        Session s = sessionService.createSession(title, otp, saved);
        otpService.sendOtpTo("faculty-console", otp);
        return ResponseEntity.ok(Map.of("sessionId", s.getId(), "otp", otp));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinSession(@RequestBody com.quiz.dto.SessionDTOs.JoinSessionRequest body) {
        Long sessionId = body.getSessionId();
        if (sessionId == null) return ResponseEntity.badRequest().body(Map.of("error","invalid sessionId"));
        String otp = body.getOtp();
        boolean ok = sessionService.validateOtp(sessionId, otp);
        if (!ok) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","invalid otp"));
        return ResponseEntity.ok(Map.of("status","ok"));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable("id") Long id) {
        List<Question> q = sessionService.getQuestionsFor(id);
        if (q.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","no questions or invalid session"));
        return ResponseEntity.ok(q);
    }
}
