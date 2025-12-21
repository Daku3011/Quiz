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
    public ResponseEntity<?> generateFromText(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        String countStr = body.getOrDefault("count", "60");
        int count = 60;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
        }

        if (text == null || text.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "no text"));
        List<Question> q = aiService.generateQuestions(text, count);
        return ResponseEntity.ok(q);
    }
}
