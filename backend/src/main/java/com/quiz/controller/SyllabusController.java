package com.quiz.controller;

import com.quiz.model.Question;
import com.quiz.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// This controller is all about the syllabus. 
// It takes the raw text we get from the faculty and sends it over to the AI service to build our question list.
@RestController
@RequestMapping("/api/syllabus")
public class SyllabusController {
    private final AIService aiService;

    public SyllabusController(AIService aiService) {
        this.aiService = aiService;
    }

    // This is the main endpoint for generating questions from syllabus text.
    @PostMapping("/generate")
    public ResponseEntity<?> generateFromText(@RequestBody Map<String, Object> body) {
        String text = (String) body.get("text");
        String fileData = (String) body.get("fileData"); // Base64 encoded file
        String mimeType = (String) body.get("mimeType"); // e.g. application/pdf

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> weights = (List<Map<String, Object>>) body.get("weights");

        String countStr = body.getOrDefault("count", "60").toString();
        int count = 60;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
        }

        if ((text == null || text.isBlank()) && (fileData == null || fileData.isBlank()))
            return ResponseEntity.badRequest().body(Map.of("error", "no text or fileData provided"));

        List<Question> q = aiService.generateQuestions(text, fileData, mimeType, count, weights);
        return ResponseEntity.ok(q);
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeSyllabus(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        org.slf4j.LoggerFactory.getLogger(SyllabusController.class).info(
                "Received analysis request for syllabus (Length: {})",
                text != null ? text.length() : "null");

        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "no text provided"));
        }
        Map<String, Object> analysis = aiService.analyzeSyllabus(text);
        return ResponseEntity.ok(analysis);
    }
}
