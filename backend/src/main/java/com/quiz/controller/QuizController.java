package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuestionRepository questionRepo;
    private final com.quiz.repository.SubmissionRepository submissionRepo;

    public QuizController(QuestionRepository questionRepo, com.quiz.repository.SubmissionRepository submissionRepo) {
        this.questionRepo = questionRepo;
        this.submissionRepo = submissionRepo;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, Object> body) {
        // body: { sessionId, studentId, answers: [{questionId, selectedOption}] }
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        if (answers == null)
            return ResponseEntity.badRequest().body("No answers provided");

        AtomicInteger score = new AtomicInteger(0);
        answers.forEach(ans -> {
            Long qId = ((Number) ans.get("questionId")).longValue();
            String selected = (String) ans.get("selectedOption");

            questionRepo.findById(qId).ifPresent(q -> {
                if (q.getCorrect() != null && q.getCorrect().equalsIgnoreCase(selected)) {
                    score.incrementAndGet();
                }
            });
        });

        // Save submission
        try {
            Long sessionId = Long.valueOf(body.get("sessionId").toString());
            Long studentId = Long.valueOf(body.get("studentId").toString());

            com.quiz.model.Submission sub = new com.quiz.model.Submission();
            sub.setSessionId(sessionId);
            sub.setStudentId(studentId);
            sub.setScore(score.get());
            submissionRepo.save(sub);
        } catch (Exception e) {
            e.printStackTrace();
            // Don't fail the request if saving fails, just log it
        }

        return ResponseEntity.ok(Map.of("score", score.get(), "message", "Submission successful"));
    }
}
