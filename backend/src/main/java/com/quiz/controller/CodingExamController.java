package com.quiz.controller;

import com.quiz.model.*;
import com.quiz.repository.*;
import com.quiz.service.AIService;
import com.quiz.service.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CodingExamController {

    private static final Logger logger = LoggerFactory.getLogger(CodingExamController.class);

    private final SessionRepository sessionRepo;
    private final StudentRepository studentRepo;
    private final CodingSubmissionRepository codingSubmissionRepo;
    private final OTPService otpService;
    private final AIService aiService;

    public CodingExamController(SessionRepository sessionRepo,
                                StudentRepository studentRepo,
                                CodingSubmissionRepository codingSubmissionRepo,
                                OTPService otpService,
                                AIService aiService) {
        this.sessionRepo = sessionRepo;
        this.studentRepo = studentRepo;
        this.codingSubmissionRepo = codingSubmissionRepo;
        this.otpService = otpService;
        this.aiService = aiService;
    }

    /**
     * Start a new coding exam session.
     */
    @PostMapping("/session/start-coding")
    public ResponseEntity<?> startCodingSession(@RequestBody Map<String, Object> body) {
        try {
            logger.info("Request to start coding session: Title='{}'", body.get("title"));

            String title = body.get("title") == null ? "Coding Exam" : body.get("title").toString();
            String language = body.get("programmingLanguage") == null ? "python" : body.get("programmingLanguage").toString();
            String probEven = body.get("problemStatementEven") == null ? "" : body.get("problemStatementEven").toString();
            String probOdd = body.get("problemStatementOdd") == null ? "" : body.get("problemStatementOdd").toString();
            Integer duration = body.get("durationMinutes") == null ? 60 : Integer.parseInt(body.get("durationMinutes").toString());

            // Generate secure OTP
            OTPService.OtpResult otpResult = otpService.generateSecureOtp();

            Session s = new Session();
            s.setTitle(title);
            s.setOtpDetails(otpResult.otpEntity);
            s.setActive(true);
            s.setNumberOfSets(2); // Problem A & B represent 2 sets
            s.setStartTime(Instant.now());
            s.setEndTime(Instant.now().plus(java.time.Duration.ofMinutes(duration)));

            // Coding exam settings
            s.setCoding(true);
            s.setProgrammingLanguage(language);
            s.setProblemStatementEven(probEven);
            s.setProblemStatementOdd(probOdd);

            Session saved = sessionRepo.save(s);
            otpService.sendOtpTo("faculty-console", otpResult.plainCode);

            logger.info("Coding session started: ID={}, OTP={}", saved.getId(), otpResult.plainCode);
            return ResponseEntity.ok(Map.of("sessionId", saved.getId(), "otp", otpResult.plainCode));
        } catch (Exception e) {
            logger.error("Failed to start coding session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start coding session: " + e.getMessage()));
        }
    }

    /**
     * Retrieve assigned coding details for a student.
     */
    @GetMapping("/session/{id}/coding-details")
    public ResponseEntity<?> getCodingDetails(@PathVariable("id") Long sessionId,
                                              @RequestParam("studentId") Long studentId) {
        Session session = sessionRepo.findById(sessionId).orElse(null);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Session not found"));
        }
        if (!session.isCoding()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Not a coding session"));
        }

        Student student = studentRepo.findById(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Student not found"));
        }

        // Validate time
        Instant now = Instant.now();
        if (session.getStartTime() != null && now.isBefore(session.getStartTime())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Exam has not started yet"));
        }
        if (session.getEndTime() != null && now.isAfter(session.getEndTime())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Exam has ended"));
        }

        // Determine even/odd
        boolean isEven = isEnrollmentEven(student.getEnrollment());
        String assignedProblem = isEven ? session.getProblemStatementEven() : session.getProblemStatementOdd();
        String problemName = isEven ? "Problem A (Even Enrollment)" : "Problem B (Odd Enrollment)";

        Map<String, Object> details = new HashMap<>();
        details.put("sessionId", session.getId());
        details.put("title", session.getTitle());
        details.put("programmingLanguage", session.getProgrammingLanguage());
        details.put("problemStatement", assignedProblem);
        details.put("assignedProblemName", problemName);
        details.put("durationMinutes", session.getEndTime() != null ? 
                java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes() : 60);
        details.put("endTime", session.getEndTime() != null ? session.getEndTime().toString() : "");
        details.put("studentName", student.getName());
        details.put("studentEnrollment", student.getEnrollment());

        return ResponseEntity.ok(details);
    }

    /**
     * Submit coding solution.
     */
    @PostMapping("/quiz/submit-code")
    public ResponseEntity<?> submitCode(@RequestBody Map<String, Object> body) {
        try {
            Long sessionId = Long.valueOf(body.get("sessionId").toString());
            Long studentId = Long.valueOf(body.get("studentId").toString());
            String code = body.get("code") == null ? "" : body.get("code").toString();
            boolean cheated = body.containsKey("cheated") && Boolean.parseBoolean(body.get("cheated").toString());

            // Auto-recovery support or check student exists
            Student student = studentRepo.findById(studentId).orElse(null);
            if (student == null) {
                if (body.containsKey("name") && body.containsKey("enrollment")) {
                    student = new Student();
                    student.setName((String) body.get("name"));
                    student.setEnrollment((String) body.get("enrollment"));
                    student.setSessionId(sessionId);
                    student = studentRepo.save(student);
                    studentId = student.getId();
                    logger.warn("Recovered student for coding submission: ID={}", studentId);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Student session not found."));
                }
            }

            // Check if already submitted
            if (codingSubmissionRepo.existsByStudentIdAndSessionId(studentId, sessionId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "You have already submitted this exam!"));
            }

            Session session = sessionRepo.findById(sessionId).orElse(null);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Session not found"));
            }

            // Determine even/odd problem statement
            boolean isEven = isEnrollmentEven(student.getEnrollment());
            String assignedProblemStatement = isEven ? session.getProblemStatementEven() : session.getProblemStatementOdd();
            String assignedProblemName = isEven ? "Problem A (Even)" : "Problem B (Odd)";

            // AI Grading
            Map<String, Object> aiResult;
            if (cheated) {
                aiResult = Map.of(
                        "marks", 0,
                        "correctnessFeedback", "Disqualified due to cheating/focus loss.",
                        "codeQualityFeedback", "N/A",
                        "aiGeneratedProbability", 0.0,
                        "aiDetectionExplanation", "Student exited fullscreen or switched tabs.",
                        "overallFeedback", "Disqualified."
                );
            } else {
                aiResult = aiService.analyzeCodingSubmission(assignedProblemStatement, code, session.getProgrammingLanguage());
            }

            CodingSubmission sub = new CodingSubmission();
            sub.setSessionId(sessionId);
            sub.setStudentId(studentId);
            sub.setCode(code);
            sub.setProgrammingLanguage(session.getProgrammingLanguage());
            sub.setAssignedProblem(assignedProblemName);
            sub.setCheated(cheated);

            Integer marks = ((Number) aiResult.getOrDefault("marks", 0)).intValue();
            sub.setScore(marks);

            String correctness = (String) aiResult.getOrDefault("correctnessFeedback", "");
            String quality = (String) aiResult.getOrDefault("codeQualityFeedback", "");
            String overall = (String) aiResult.getOrDefault("overallFeedback", "");
            sub.setAiFeedback("### Correctness\n" + correctness + "\n\n### Code Quality\n" + quality + "\n\n### Overall Feedback\n" + overall);

            Double aiProb = ((Number) aiResult.getOrDefault("aiGeneratedProbability", 0.0)).doubleValue();
            sub.setAiGeneratedProbability(aiProb);

            String aiExplain = (String) aiResult.getOrDefault("aiDetectionExplanation", "");
            sub.setAiDetectionExplanation(aiExplain);

            codingSubmissionRepo.save(sub);

            logger.info("Coding Submission scored: Student={}, Session={}, Marks={}, AI%={}",
                    studentId, sessionId, marks, aiProb);

            return ResponseEntity.ok(Map.of(
                    "score", marks,
                    "aiFeedback", sub.getAiFeedback(),
                    "aiGeneratedProbability", aiProb,
                    "aiDetectionExplanation", aiExplain,
                    "cheated", cheated
            ));
        } catch (Exception e) {
            logger.error("Failed to submit coding solution", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Submission failed: " + e.getMessage()));
        }
    }

    /**
     * Retrieve all results for a coding session.
     */
    @GetMapping("/session/{id}/coding-results")
    public ResponseEntity<?> getCodingResults(@PathVariable("id") Long sessionId) {
        List<CodingSubmission> subs = codingSubmissionRepo.findBySessionId(sessionId);
        List<Map<String, Object>> results = new ArrayList<>();

        for (CodingSubmission s : subs) {
            Student student = studentRepo.findById(s.getStudentId()).orElse(null);
            Map<String, Object> map = new HashMap<>();
            map.put("submissionId", s.getId());
            map.put("studentId", s.getStudentId());
            map.put("studentName", student != null ? student.getName() : "Unknown");
            map.put("enrollment", student != null ? student.getEnrollment() : "Unknown");
            map.put("assignedProblem", s.getAssignedProblem());
            map.put("programmingLanguage", s.getProgrammingLanguage());
            map.put("score", s.getScore());
            map.put("aiGeneratedProbability", s.getAiGeneratedProbability());
            map.put("cheated", s.isCheated());
            map.put("submittedAt", s.getSubmittedAt().toString());
            results.add(map);
        }

        // Sort by score descending
        results.sort((a, b) -> Integer.compare((Integer) b.get("score"), (Integer) a.get("score")));

        return ResponseEntity.ok(results);
    }

    /**
     * Retrieve complete coding submission details.
     */
    @GetMapping("/coding-submission/{id}/details")
    public ResponseEntity<?> getSubmissionDetails(@PathVariable("id") Long subId) {
        CodingSubmission sub = codingSubmissionRepo.findById(subId).orElse(null);
        if (sub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Submission not found"));
        }

        Student student = studentRepo.findById(sub.getStudentId()).orElse(null);
        Session session = sessionRepo.findById(sub.getSessionId()).orElse(null);

        Map<String, Object> map = new HashMap<>();
        map.put("submissionId", sub.getId());
        map.put("studentName", student != null ? student.getName() : "Unknown");
        map.put("enrollment", student != null ? student.getEnrollment() : "Unknown");
        map.put("assignedProblem", sub.getAssignedProblem());
        map.put("programmingLanguage", sub.getProgrammingLanguage());
        map.put("code", sub.getCode());
        map.put("score", sub.getScore());
        map.put("aiFeedback", sub.getAiFeedback());
        map.put("aiGeneratedProbability", sub.getAiGeneratedProbability());
        map.put("aiDetectionExplanation", sub.getAiDetectionExplanation());
        map.put("cheated", sub.isCheated());
        map.put("submittedAt", sub.getSubmittedAt().toString());

        if (session != null) {
            boolean isEven = student != null ? isEnrollmentEven(student.getEnrollment()) : 
                (sub.getAssignedProblem() != null && sub.getAssignedProblem().contains("Even"));
            map.put("problemStatement", isEven ? session.getProblemStatementEven() : session.getProblemStatementOdd());
        }

        return ResponseEntity.ok(map);
    }

    /**
     * Override coding submission marks.
     */
    @PostMapping("/coding-submission/{id}/override-marks")
    public ResponseEntity<?> overrideMarks(@PathVariable("id") Long subId, @RequestBody Map<String, Object> body) {
        CodingSubmission sub = codingSubmissionRepo.findById(subId).orElse(null);
        if (sub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Submission not found"));
        }

        try {
            Integer marks = Integer.parseInt(body.get("marks").toString());
            if (marks < 0 || marks > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Marks must be between 0 and 100"));
            }

            sub.setScore(marks);
            codingSubmissionRepo.save(sub);
            logger.info("Marks overridden manually for submission {}: new marks={}", subId, marks);

            return ResponseEntity.ok(Map.of("status", "success", "marks", marks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid marks value"));
        }
    }

    /**
     * Enrollment calculation utility.
     */
    private boolean isEnrollmentEven(String enrollment) {
        if (enrollment == null || enrollment.isBlank()) {
            return true;
        }
        for (int i = enrollment.length() - 1; i >= 0; i--) {
            char c = enrollment.charAt(i);
            if (Character.isDigit(c)) {
                return Character.getNumericValue(c) % 2 == 0;
            }
        }
        return Math.abs(enrollment.hashCode()) % 2 == 0;
    }
}
