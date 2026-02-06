package com.quiz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.model.Question;
import com.quiz.model.Submission;
import com.quiz.repository.QuestionRepository;
import com.quiz.repository.SubmissionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final SubmissionRepository submissionRepo;
    private final QuestionRepository questionRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalyticsController(SubmissionRepository submissionRepo, QuestionRepository questionRepo) {
        this.submissionRepo = submissionRepo;
        this.questionRepo = questionRepo;
    }

    /**
     * Aggregated analytics for the entire session (Average, High/Low, CO
     * performance across all students).
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSessionAnalytics(@PathVariable("sessionId") Long sessionId) {
        List<Submission> submissions = submissionRepo.findBySessionId(sessionId);

        if (submissions.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "totalSubmissions", 0,
                    "averageScore", 0,
                    "highestScore", 0,
                    "lowestScore", 0,
                    "coAnalysis", Collections.emptyList()));
        }

        double totalScore = 0;
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;

        // Structures for aggregation
        // CO -> [correctCount, totalCount]
        Map<String, int[]> coStats = new HashMap<>();

        for (Submission sub : submissions) {
            int score = sub.getScore();
            totalScore += score;
            if (score > maxScore)
                maxScore = score;
            if (score < minScore)
                minScore = score;

            // Parse details for CO analysis
            try {
                if (sub.getDetails() != null) {
                    List<Map<String, Object>> answers = objectMapper.readValue(sub.getDetails(), new TypeReference<>() {
                    });
                    for (Map<String, Object> ans : answers) {
                        Long qId = ((Number) ans.get("questionId")).longValue();
                        String selected = (String) ans.get("selectedOption");

                        questionRepo.findById(qId).ifPresent(q -> {
                            String co = q.getCourseOutcome();
                            if (co != null && !co.isBlank()) {
                                coStats.putIfAbsent(co, new int[] { 0, 0 });
                                coStats.get(co)[1]++; // Total attempts
                                if (q.getCorrect() != null && q.getCorrect().equalsIgnoreCase(selected)) {
                                    coStats.get(co)[0]++; // Correct
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                // Log and continue
                e.printStackTrace();
            }
        }

        // Format CO Analysis
        List<Map<String, Object>> coAnalysis = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : coStats.entrySet()) {
            String co = entry.getKey();
            int correct = entry.getValue()[0];
            int total = entry.getValue()[1];
            double accuracy = total > 0 ? ((double) correct / total) * 100 : 0;

            // Round to 1 decimal
            accuracy = Math.round(accuracy * 10.0) / 10.0;

            coAnalysis.add(Map.of(
                    "co", co,
                    "accuracy", accuracy,
                    "correct", correct,
                    "total", total));
        }

        // Sort COs
        coAnalysis.sort(Comparator.comparing(m -> (String) m.get("co")));

        // Calculate average formatted
        String avgScore = String.format("%.2f", totalScore / submissions.size());

        return ResponseEntity.ok(Map.of(
                "totalSubmissions", submissions.size(),
                "averageScore", avgScore,
                "highestScore", maxScore,
                "lowestScore", minScore,
                "coAnalysis", coAnalysis));
    }

    // Detailed analytics for a single student's submission in a session.
    @GetMapping("/student/{studentId}/session/{sessionId}")
    public ResponseEntity<?> getStudentSessionAnalytics(@PathVariable("studentId") Long studentId,
            @PathVariable("sessionId") Long sessionId) {
        return submissionRepo.findByStudentIdAndSessionId(studentId, sessionId)
                .map(sub -> {
                    try {
                        List<Map<String, Object>> answers;
                        if (sub.getDetails() != null) {
                            answers = objectMapper.readValue(sub.getDetails(), new TypeReference<>() {
                            });
                        } else {
                            answers = Collections.emptyList();
                        }

                        // Enrich answers
                        List<Map<String, Object>> enrichedAnswers = new ArrayList<>();
                        Map<String, int[]> coStats = new HashMap<>();

                        for (Map<String, Object> ans : answers) {
                            Long qId = ((Number) ans.get("questionId")).longValue();
                            String selected = (String) ans.get("selectedOption");

                            Map<String, Object> enriched = new HashMap<>();
                            enriched.put("questionId", qId);
                            enriched.put("selected", selected);

                            // Default values
                            enriched.put("questionText", "Unknown Question (ID: " + qId + ")");
                            enriched.put("correctOption", "?");
                            enriched.put("explanation", "");
                            enriched.put("isCorrect", false);
                            enriched.put("co", "N/A");

                            Optional<Question> qOpt = questionRepo.findById(qId);
                            if (qOpt.isPresent()) {
                                Question q = qOpt.get();
                                enriched.put("questionText", q.getText());
                                enriched.put("correctOption", q.getCorrect());
                                enriched.put("explanation", q.getExplanation());
                                enriched.put("co", q.getCourseOutcome());

                                boolean isCorrect = q.getCorrect() != null && q.getCorrect().equalsIgnoreCase(selected);
                                enriched.put("isCorrect", isCorrect);

                                // CO Stats
                                String co = q.getCourseOutcome();
                                if (co != null && !co.isBlank()) {
                                    coStats.putIfAbsent(co, new int[] { 0, 0 });
                                    coStats.get(co)[1]++; // Total
                                    if (isCorrect) {
                                        coStats.get(co)[0]++; // Correct
                                    }
                                }
                            }
                            enrichedAnswers.add(enriched);
                        }

                        // CO Performance List
                        List<Map<String, Object>> coPerformance = new ArrayList<>();
                        for (Map.Entry<String, int[]> entry : coStats.entrySet()) {
                            String co = entry.getKey();
                            int correct = entry.getValue()[0];
                            int total = entry.getValue()[1];
                            double pct = total > 0 ? ((double) correct / total) * 100 : 0;
                            // Round to 1 decimal
                            pct = Math.round(pct * 10.0) / 10.0;
                            coPerformance.add(Map.of("co", co, "percentage", pct));
                        }
                        coPerformance.sort(Comparator.comparing(m -> (String) m.get("co")));

                        Map<String, Object> response = new HashMap<>();
                        response.put("score", sub.getScore());
                        // If score is 0 and cheated is false but answers exist, it might be just a 0 score.
                        // But if cheated is true, score is forced to 0.
                        response.put("cheated", sub.isCheated());
                        response.put("answers", enrichedAnswers);
                        response.put("coPerformance", coPerformance);

                        return ResponseEntity.ok(response);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().body("Error processing submission details");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
