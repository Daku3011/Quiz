package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles quiz submissions from students.
 * Caclulates scores, assigns question sets, and returns detailed results.
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(QuizController.class);

    private final QuestionRepository questionRepo;
    private final com.quiz.repository.SubmissionRepository submissionRepo;
    private final com.quiz.repository.SessionRepository sessionRepo;

    public QuizController(QuestionRepository questionRepo, com.quiz.repository.SubmissionRepository submissionRepo,
            com.quiz.repository.SessionRepository sessionRepo) {
        this.questionRepo = questionRepo;
        this.submissionRepo = submissionRepo;
        this.sessionRepo = sessionRepo;
    }

    /**
     * Processes a quiz submission.
     * Calculates score based on correct answers, prevents duplicate submissions
     * (logic pending),
     * and returns the result with explanations.
     */
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

            // Calculate Set
            sessionRepo.findById(sessionId).ifPresent(session -> {
                int sets = session.getNumberOfSets();
                if (sets < 1)
                    sets = 1;
                long setIndex = studentId % sets;
                char setChar = (char) ('A' + setIndex);
                sub.setQuestionSet("Set " + setChar);
            });

            submissionRepo.save(sub);
            logger.info("Submission saved: Session={}, Student={}, Score={}", sessionId, studentId, score.get());
        } catch (Exception e) {
            logger.error("Error saving submission", e);
            // Don't fail the request if saving fails, just log it
        }

        return ResponseEntity.ok(Map.of(
                "score", score.get(),
                "message", "Submission successful",
                "results", generateResults(answers)));
    }

    private List<Map<String, Object>> generateResults(List<Map<String, Object>> answers) {
        return answers.stream().map(ans -> {
            Long qId = ((Number) ans.get("questionId")).longValue();
            String selected = (String) ans.get("selectedOption");
            Map<String, Object> res = new java.util.HashMap<>();
            res.put("questionId", qId);
            res.put("selected", selected);

            questionRepo.findById(qId).ifPresent(q -> {
                res.put("correctOption", q.getCorrect());
                res.put("explanation", q.getExplanation());
                res.put("isCorrect", q.getCorrect() != null && q.getCorrect().equalsIgnoreCase(selected));
                res.put("text", q.getText());
                res.put("optionA", q.getOptionA());
                res.put("optionB", q.getOptionB());
                res.put("optionC", q.getOptionC());
                res.put("optionD", q.getOptionD());
            });
            return res;
        }).collect(java.util.stream.Collectors.toList());
    }
}
