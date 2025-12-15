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
    // A static list of high-quality engineering questions for mock generation
    private static final List<Question> MOCK_DB = new ArrayList<>();

    static {
        addQ("Which of the following is NOT a feature of Java?",
                "Pointers", "Object-Oriented", "Portable", "Dynamic", "A");
        addQ("In Operating Systems, what is 'Thrashing'?",
                "Excessive paging activity", "High CPU utilization", "Deadlock condition", "Memory leak", "A");
        addQ("What is the time complexity of Binary Search?",
                "O(log n)", "O(n)", "O(n log n)", "O(1)", "A");
        addQ("Which data structure uses LIFO principle?",
                "Stack", "Queue", "Linked List", "Tree", "A");
        addQ("In DBMS, what does ACID stand for?",
                "Atomicity, Consistency, Isolation, Durability", "Atomicity, Concurrency, Isolation, Database",
                "Availability, Consistency, Isolation, Durability", "Auto, Consistency, Internal, Data", "A");
        addQ("Which layer of OSI model is responsible for routing?",
                "Network Layer", "Data Link Layer", "Transport Layer", "Session Layer", "A");
        addQ("What is the purpose of the 'volatile' keyword in Java?",
                "Indicates variable may change unexpectedly", "Makes variable constant", "Optimizes variable access",
                "Prevents serialization", "A");
        addQ("Which sorting algorithm has the best worst-case time complexity?",
                "Merge Sort", "Quick Sort", "Bubble Sort", "Insertion Sort", "A");
        addQ("In OOP, what is Polymorphism?",
                "Ability to take multiple forms", "Hiding implementation details", "Wrapping data and code",
                "Creating new classes from existing", "A");
        addQ("What is a Semaphore in OS?",
                "A signaling mechanism", "A type of memory", "A CPU register", "A file system", "A");
    }

    private static void addQ(String text, String a, String b, String c, String d, String ans) {
        Question q = new Question();
        q.setText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrect(ans);
        MOCK_DB.add(q);
    }

    @org.springframework.beans.factory.annotation.Value("${gemini.api.key}")
    private String apiKey;

    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
    private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public List<Question> generateQuestions(String syllabusText, int count) {
        // 1. Check if API key is provided
        if (apiKey == null || apiKey.isBlank() || apiKey.contains("INSERT_YOUR_GEMINI_KEY")) {
            System.out.println("Using Mock DB (No API Key provided)");
            return generateMockQuestions(count);
        }

        // 2. Call Gemini API
        try {
            String prompt = "Generate " + count + " multiple-choice questions based on the following syllabus. " +
                    "Return ONLY a raw JSON array (no markdown, no code blocks) of objects with keys: " +
                    "text, optionA, optionB, optionC, optionD, correct (A/B/C/D). " +
                    "Syllabus: " + syllabusText;

            // Correct structure for Gemini API: { "contents": [{ "parts": [{ "text": "..."
            // }] }] }
            // Using a cleaner way to build JSON string to avoid Jackson complexity if not
            // fully configured
            String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": " + mapper.writeValueAsString(prompt)
                    + " }] }] }";

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(
                            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                                    + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse response
                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());
                String responseText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text")
                        .asText();

                // Clean up markdown if present
                responseText = responseText.replaceAll("```json", "").replaceAll("```", "").trim();

                List<Question> questions = mapper.readValue(responseText,
                        new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                        });
                return questions;
            } else {
                System.err.println("Gemini API Error: " + response.statusCode() + " " + response.body());
                return generateMockQuestions(count);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return generateMockQuestions(count);
        }
    }

    private List<Question> generateMockQuestions(int count) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Question original = MOCK_DB.get(i % MOCK_DB.size());
            Question q = new Question();
            q.setText(original.getText());
            q.setOptionA(original.getOptionA());
            q.setOptionB(original.getOptionB());
            q.setOptionC(original.getOptionC());
            q.setOptionD(original.getOptionD());
            q.setCorrect(original.getCorrect());
            questions.add(q);
        }
        return questions;
    }
}
