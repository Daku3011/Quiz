package com.quiz.controller;

import com.quiz.model.Submission;
import com.quiz.repository.SubmissionRepository;
import com.quiz.repository.StudentRepository;
import com.quiz.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles administrative tasks such as creating faculty accounts and viewing
 * all submissions.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SubmissionRepository submissionRepo;
    private final StudentRepository studentRepo;

    private final UserRepository userRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AdminController(SubmissionRepository submissionRepo, StudentRepository studentRepo,
            com.quiz.repository.UserRepository userRepo,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.submissionRepo = submissionRepo;
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new Faculty account.
     * 
     * @param body map containing "username" and "password"
     * @return Success message or error if username exists.
     */
    @PostMapping("/faculty")
    public ResponseEntity<?> createFaculty(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body("Username and password required");
        }

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        com.quiz.model.User user = new com.quiz.model.User(username, passwordEncoder.encode(password),
                com.quiz.model.Role.FACULTY);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Faculty created successfully", "id", user.getId()));
    }

    /**
     * Retrieves all student submissions across all sessions.
     * 
     * @return List of submission details including student name and score.
     */
    @GetMapping("/submissions")
    public ResponseEntity<?> getAllSubmissions() {
        List<Submission> allSubmissions = submissionRepo.findAll();

        List<Map<String, Object>> result = allSubmissions.stream().map(s -> {
            Long sid = s.getStudentId();
            String studentName = (sid != null)
                    ? studentRepo.findById(sid).map(com.quiz.model.Student::getName).orElse("Unknown")
                    : "Unknown";

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("sessionId", s.getSessionId());
            map.put("studentName", studentName);
            map.put("score", s.getScore());
            map.put("submittedAt", s.getSubmittedAt() != null ? s.getSubmittedAt().toString() : "N/A");

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
