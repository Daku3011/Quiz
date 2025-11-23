package com.quiz.service;

import com.quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {
    // Placeholder: replace with real LLM call. For now produce simple questions.
    // To implement real AI:
    // 1. Add OpenAiService service = new OpenAiService("your-key");
    // 2. Create ChatCompletionRequest with the syllabus text.
    // 3. Parse response into Question objects.
    public List<Question> generateQuestions(String syllabusText, int count) {
        List<Question> questions = new ArrayList<>();
        if (syllabusText == null || syllabusText.isBlank())
            return questions;
        String[] parts = syllabusText.split("\\.\\s+");

        // Ensure we generate exactly 'count' questions
        for (int i = 0; i < count; i++) {
            // Cycle through parts if count > parts.length
            String s = parts[i % parts.length].trim();
            if (s.isEmpty())
                s = "General knowledge from syllabus";

            Question q = new Question();
            q.setText("Q" + (i + 1) + ": Which statement best describes: "
                    + (s.length() > 50 ? s.substring(0, 50) + "..." : s) + "?");
            q.setOptionA("Option A: Definition of " + s.substring(0, Math.min(10, s.length())));
            q.setOptionB("Option B: Opposite of " + s.substring(0, Math.min(10, s.length())));
            q.setOptionC("Option C: Unrelated concept");
            q.setOptionD("Option D: None of the above");
            q.setCorrect("A");
            questions.add(q);
        }
        return questions;
    }
}
