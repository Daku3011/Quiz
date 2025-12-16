package com.quiz.service;

import com.quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                                "Excessive paging activity", "High CPU utilization", "Deadlock condition",
                                "Memory leak", "A");
                addQ("What is the time complexity of Binary Search?",
                                "O(log n)", "O(n)", "O(n log n)", "O(1)", "A");
                addQ("Which data structure uses LIFO principle?",
                                "Stack", "Queue", "Linked List", "Tree", "A");
                addQ("In DBMS, what does ACID stand for?",
                                "Atomicity, Consistency, Isolation, Durability",
                                "Atomicity, Concurrency, Isolation, Database",
                                "Availability, Consistency, Isolation, Durability", "Auto, Consistency, Internal, Data",
                                "A");
                addQ("Which layer of OSI model is responsible for routing?",
                                "Network Layer", "Data Link Layer", "Transport Layer", "Session Layer", "A");
                addQ("What is the purpose of the 'volatile' keyword in Java?",
                                "Indicates variable may change unexpectedly", "Makes variable constant",
                                "Optimizes variable access",
                                "Prevents serialization", "A");
                addQ("Which sorting algorithm has the best worst-case time complexity?",
                                "Merge Sort", "Quick Sort", "Bubble Sort", "Insertion Sort", "A");
                addQ("In OOP, what is Polymorphism?",
                                "Ability to take multiple forms", "Hiding implementation details",
                                "Wrapping data and code",
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

        @org.springframework.beans.factory.annotation.Value("${openai.api.key}")
        private String apiKey;

        private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        public List<Question> generateQuestions(String syllabusText, int count) {
                // 1. Check if API key is provided
                if (apiKey == null || apiKey.isBlank() || apiKey.contains("INSERT_YOUR_OPEN_AI_KEY")) {
                        System.out.println("Using Mock DB (No API Key provided)");
                        return generateMockQuestions(count);
                }

                // 2. Call OpenAI API
                try {
                        String systemPrompt = "You are a helpful assistant that generates multiple-choice questions.";
                        String userPrompt = "Generate " + count
                                        + " multiple-choice questions based on the following syllabus.\n" +
                                        "Return ONLY a raw JSON array (no markdown, no code blocks, just the array) of objects with keys:\n"
                                        +
                                        "text, optionA, optionB, optionC, optionD, correct (A/B/C/D), explanation.\n" +
                                        "CRITICAL REQUIREMENTS:\n" +
                                        "1. Mix of difficulty: 30% Hard (Numerical/Code), 40% Medium, 30% Conceptual.\n"
                                        +
                                        "2. INCLUDE CODE SNIPPETS in the 'text' field where applicable (use \\n for line breaks).\n"
                                        +
                                        "3. INCLUDE NUMERICAL PROBLEMS if the topic allows.\n" +
                                        "4. Ensure questions are UNIQUE and not repetitive.\n" +
                                        "5. 'explanation' must be detailed and explain WHY the answer is correct.\n" +
                                        "Syllabus: " + syllabusText;

                        // correct structure for OpenAI: { "model": "gpt-4o", "messages": [...] }
                        Map<String, Object> bodyMap = Map.of(
                                        "model", "gpt-4o",
                                        "messages", List.of(
                                                        Map.of("role", "system", "content", systemPrompt),
                                                        Map.of("role", "user", "content", userPrompt)),
                                        "temperature", 0.7);

                        String requestBody = mapper.writeValueAsString(bodyMap);

                        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                                        .uri(java.net.URI.create("https://api.openai.com/v1/chat/completions"))
                                        .header("Content-Type", "application/json")
                                        .header("Authorization", "Bearer " + apiKey)
                                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                                        .build();

                        java.net.http.HttpResponse<String> response = httpClient.send(request,
                                        java.net.http.HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                                // Parse response
                                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());
                                String responseText = root.path("choices").get(0).path("message").path("content")
                                                .asText();

                                // Clean up markdown if present (sometimes gpt adds ```json ... ```)
                                responseText = responseText.replaceAll("```json", "").replaceAll("```", "").trim();

                                List<Question> questions = mapper.readValue(responseText,
                                                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                });
                                return questions;
                        } else {
                                System.err.println(
                                                "OpenAI API Error: " + response.statusCode() + " " + response.body());
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
