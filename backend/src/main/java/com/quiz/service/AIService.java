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

        @org.springframework.beans.factory.annotation.Value("${ollama.api.url}")
        private String ollamaUrl;

        @org.springframework.beans.factory.annotation.Value("${ollama.model}")
        private String ollamaModel;

        private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // Batch size for parallel requests. Increased to 10 for higher throughput.
        private static final int BATCH_SIZE = 10;

        public List<Question> generateQuestions(String syllabusText, int count) {
                if (ollamaUrl == null || ollamaUrl.isBlank()) {
                        System.out.println("Using Mock DB (No Ollama URL provided)");
                        return generateMockQuestions(count);
                }

                System.out.println("DEBUG: Generating " + count + " questions using PARALLEL BATCHES (Size: "
                                + BATCH_SIZE + ")...");
                List<java.util.concurrent.CompletableFuture<List<Question>>> futures = new ArrayList<>();

                int questionsRemaining = count;
                while (questionsRemaining > 0) {
                        int currentBatchSize = Math.min(BATCH_SIZE, questionsRemaining);
                        questionsRemaining -= currentBatchSize;

                        // Capture effective final variable for lambda
                        final int batchCount = currentBatchSize;

                        futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                                try {
                                        return generateBatch(syllabusText, batchCount);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                        return new ArrayList<>(); // Return empty on failure to avoid nulls
                                }
                        }));
                }

                List<Question> allQuestions = futures.stream()
                                .map(java.util.concurrent.CompletableFuture::join)
                                .flatMap(List::stream)
                                .collect(java.util.stream.Collectors.toList());

                if (allQuestions.isEmpty()) {
                        System.out.println("DEBUG: All batch requests failed, returning mocks.");
                        return generateMockQuestions(count);
                }

                System.out.println("DEBUG: Successfully generated " + allQuestions.size() + " questions.");
                return allQuestions;
        }

        private List<Question> generateBatch(String syllabusText, int count) throws Exception {
                // Strict prompt for batch generation
                String systemPrompt = "You are a precise assistant that generates JSON arrays of questions.";
                String userPrompt = "Generate EXACTLY " + count
                                + " multiple-choice questions based on the reference text.\n" +
                                "Return the response as a valid JSON ARRAY of objects. Do NOT return a single object.\n"
                                +
                                "JSON Format:\n"
                                +
                                "[\n"
                                +
                                "  {\"text\": \"...\", \"optionA\": \"...\", \"optionB\": \"...\", \"optionC\": \"...\", \"optionD\": \"...\", \"correct\": \"A\", \"explanation\": \"...\"},\n"
                                +
                                "  ... (make sure there are " + count + " objects)\n"
                                +
                                "]\n"
                                +
                                "Required Keys: text, optionA, optionB, optionC, optionD, correct (A/B/C/D), explanation.\n"
                                +
                                "CRITICAL REQUIREMENTS:\n" +
                                "1. Mix of difficulty: 30% Hard, 40% Medium, 30% Conceptual.\n" +
                                "2. INCLUDE CODE SNIPPETS in the 'text' field where applicable.\n" +
                                "3. Do NOT copy sentences from syllabus verbatim; rephrase them as questions.\n" +
                                "4. 'explanation' must be concise (max 30 words).\n" +
                                "Syllabus: " + syllabusText;

                Map<String, Object> bodyMap = Map.of(
                                "model", ollamaModel,
                                "messages", List.of(
                                                Map.of("role", "system", "content", systemPrompt),
                                                Map.of("role", "user", "content", userPrompt)),
                                "stream", false,
                                "options", Map.of("num_predict", 4096, "num_ctx", 4096)); // Higher predict for batch

                String requestBody = mapper.writeValueAsString(bodyMap);

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                                .uri(java.net.URI.create(ollamaUrl))
                                .header("Content-Type", "application/json")
                                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                java.net.http.HttpResponse<String> response = httpClient.send(request,
                                java.net.http.HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());
                        String responseText = root.path("message").path("content").asText();

                        // LOGGING
                        System.out.println("DEBUG RAW AI BATCH RESPONSE: "
                                        + responseText.substring(0, Math.min(responseText.length(), 200)) + "...");

                        responseText = responseText.replaceAll("```json", "").replaceAll("```", "").trim();
                        try {
                                return mapper.readValue(responseText,
                                                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                });
                        } catch (Exception e) {
                                System.err.println("Failed to parse question JSON: " + e.getMessage());
                                return null;
                        }
                } else {
                        System.err.println("Ollama API Error: " + response.statusCode() + " " + response.body());
                        return null;
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
