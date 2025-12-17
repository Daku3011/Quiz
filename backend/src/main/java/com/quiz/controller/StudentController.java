package com.quiz.controller;

import com.quiz.model.Student;
import com.quiz.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Manages student registration.
 * Allows students to register for a session using an OTP.
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StudentController.class);
    private final StudentRepository studentRepo;

    private final com.quiz.service.SessionService sessionService;

    public StudentController(StudentRepository studentRepo, com.quiz.service.SessionService sessionService) {
        this.studentRepo = studentRepo;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String enrollment = body.get("enrollment");
            String sidStr = body.get("sessionId");
            String otp = body.get("otp");

            if (name == null || enrollment == null || sidStr == null || otp == null) {
                return ResponseEntity.badRequest().body("Missing fields");
            }

            Long sessionId = Long.parseLong(sidStr);

            // 1. Validate Session & OTP
            boolean validOtp = sessionService.validateOtp(sessionId, otp);
            if (!validOtp) {
                return ResponseEntity.status(401).body("Invalid Session ID or OTP");
            }

            // 2. Check for Duplicate Enrollment in this Session
            if (studentRepo.existsByEnrollmentAndSessionId(enrollment, sessionId)) {
                return ResponseEntity.status(409)
                        .body("Student with enrollment " + enrollment + " already registered for this session.");
            }

            // 3. Register Student
            Student s = new Student();
            s.setName(name);
            s.setEnrollment(enrollment);
            s.setSessionId(sessionId);
            studentRepo.save(s);

            return ResponseEntity.ok(Map.of("studentId", s.getId(), "message", "Registered successfully"));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid Session ID format");
        } catch (Exception e) {
            logger.error("Registration error", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
