package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// This is where the magic happens for the students. 
// It takes their submissions, calculates their scores, and makes sure they get the right set of questions.
import java.util.Collections;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(QuizController.class);

    private final QuestionRepository questionRepo;
    private final com.quiz.repository.StudentRepository studentRepo;
    private final com.quiz.repository.SubmissionRepository submissionRepo;
    private final com.quiz.repository.SessionRepository sessionRepo;

    public QuizController(QuestionRepository questionRepo, com.quiz.repository.SubmissionRepository submissionRepo,
            com.quiz.repository.SessionRepository sessionRepo, com.quiz.repository.StudentRepository studentRepo) {
        this.questionRepo = questionRepo;
        this.submissionRepo = submissionRepo;
        this.sessionRepo = sessionRepo;
        this.studentRepo = studentRepo;
    }

    // This is the main endpoint students hit when they finish their quiz.
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        if (answers == null)
            return ResponseEntity.badRequest().body("No answers provided");

        Long sid = Long.valueOf(body.get("studentId").toString());
        Long sessId = Long.valueOf(body.get("sessionId").toString());

        // Sometimes the server restarts or something goes wrong, and we lose the
        // student's session.
        // This block tries to "auto-recover" the student info from the request so they
        // don't lose their work.
        if (!studentRepo.existsById(sid)) {
            // Check if name/enrollment provided to recover
            if (body.containsKey("name") && body.containsKey("enrollment")) {
                com.quiz.model.Student newStudent = new com.quiz.model.Student();
                newStudent.setName((String) body.get("name"));
                newStudent.setEnrollment((String) body.get("enrollment"));
                newStudent.setSessionId(sessId);
                // We might want to preserve the ID if possible, but auto-gen is safer.
                // However, we need to return the new ID?
                // Actually, just save it and use the NEW ID for the submission.
                newStudent = studentRepo.save(newStudent);
                sid = newStudent.getId(); // Update local var to use valid ID
                logger.warn("Recovered missing student: OldID={} -> NewID={}", body.get("studentId"), sid);
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Session expired (Server Restarted). Please refresh and re-join."));
            }
        } else {
            if (submissionRepo.existsByStudentIdAndSessionId(sid, sessId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "You have already submitted this quiz!"));
            }
        }

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

        // Now we save the submission details so the faculty can see them later.
        try {
            Long sessionId = Long.valueOf(body.get("sessionId").toString());
            Long studentId = Long.valueOf(body.get("studentId").toString());

            com.quiz.model.Submission sub = new com.quiz.model.Submission();
            sub.setSessionId(sessionId);
            sub.setStudentId(studentId);

            boolean cheated = body.containsKey("cheated") && Boolean.parseBoolean(body.get("cheated").toString());
            if (cheated) {
                sub.setScore(0);
                sub.setCheated(true);
            } else {
                sub.setScore(score.get());
            }

            // We figure out which "set" the student was taking based on their ID.
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
