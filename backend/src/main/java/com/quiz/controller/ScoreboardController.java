package com.quiz.controller;

import com.quiz.model.Session;
import com.quiz.repository.SessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This controller handles the scoreboard data for a specific session.
// It's used by faculty to see everyone's progress in real-time.
@RestController
@RequestMapping("/api/session")
public class ScoreboardController {

    private final SessionRepository sessionRepo;
    private final com.quiz.repository.SubmissionRepository submissionRepo;
    private final com.quiz.repository.StudentRepository studentRepo;

    public ScoreboardController(SessionRepository sessionRepo, com.quiz.repository.SubmissionRepository submissionRepo,
            com.quiz.repository.StudentRepository studentRepo) {
        this.sessionRepo = sessionRepo;
        this.submissionRepo = submissionRepo;
        this.studentRepo = studentRepo;
    }

    // This method pulls together all the scores and student names for a given
    // session ID.
    @GetMapping("/{id}/scoreboard")
    public ResponseEntity<?> getScoreboard(@PathVariable("id") Long id) {
        return sessionRepo.findById(id)
                .map(session -> {
                    List<com.quiz.model.Submission> subs = submissionRepo.findBySessionId(id);
                    return ResponseEntity.ok(subs.stream().map(s -> {
                        String studentName = studentRepo.findById(s.getStudentId())
                                .map(com.quiz.model.Student::getName).orElse("Unknown");
                        String enrollment = studentRepo.findById(s.getStudentId())
                                .map(com.quiz.model.Student::getEnrollment).orElse("Unknown");

                        return Map.of(
                                "studentName", studentName,
                                "enrollment", enrollment,
                                "score", s.getScore(),
                                "questionSet", s.getQuestionSet() != null ? s.getQuestionSet() : "-",
                                "submittedAt", s.getSubmittedAt().toString(),
                                "cheated", s.isCheated());
                    }).collect(Collectors.toList()));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
