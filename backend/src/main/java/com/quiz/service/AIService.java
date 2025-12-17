package com.quiz.service;

import com.quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AIService.class);

        // A static list of high-quality engineering questions for mock generation
        private static final List<Question> MOCK_DB = new ArrayList<>();

        static {
                // ... (static block content unchanged) ...
                addQ("Which of the following is NOT a feature of Java?", "Pointers", "Object-Oriented", "Portable",
                                "Dynamic", "A");
                addQ("In Operating Systems, what is 'Thrashing'?", "Excessive paging activity", "High CPU utilization",
                                "Deadlock condition", "Memory leak", "A");
                addQ("What is the time complexity of Binary Search?", "O(log n)", "O(n)", "O(n log n)", "O(1)", "A");
                addQ("Which data structure uses LIFO principle?", "Stack", "Queue", "Linked List", "Tree", "A");
                addQ("In DBMS, what does ACID stand for?", "Atomicity, Consistency, Isolation, Durability",
                                "Atomicity, Concurrency, Isolation, Database",
                                "Availability, Consistency, Isolation, Durability", "Auto, Consistency, Internal, Data",
                                "A");
                addQ("Which layer of OSI model is responsible for routing?", "Network Layer", "Data Link Layer",
                                "Transport Layer", "Session Layer", "A");
                addQ("What is the purpose of the 'volatile' keyword in Java?",
                                "Indicates variable may change unexpectedly", "Makes variable constant",
                                "Optimizes variable access", "Prevents serialization", "A");
                addQ("Which sorting algorithm has the best worst-case time complexity?", "Merge Sort", "Quick Sort",
                                "Bubble Sort", "Insertion Sort", "A");
                addQ("In OOP, what is Polymorphism?", "Ability to take multiple forms", "Hiding implementation details",
                                "Wrapping data and code", "Creating new classes from existing", "A");
                addQ("What is a Semaphore in OS?", "A signaling mechanism", "A type of memory", "A CPU register",
                                "A file system", "A");
        }

        // ... (addQ method unchanged) ...
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
        private String geminiApiKey;

        @org.springframework.beans.factory.annotation.Value("${gemini.api.url}")
        private String geminiApiUrl;

        @org.springframework.beans.factory.annotation.Value("${gemini.model}")
        private String geminiModel;

        private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
        private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // Batch size for parallel requests. Increased to 10 for higher throughput.
        private static final int BATCH_SIZE = 10;

        /**
         * Generates multiple-choice questions based on the provided syllabus text.
         * Uses Google Gemini AI for generation, or falls back to a mock database if API
         * key is missing or calls fail.
         *
         * @param syllabusText The text content to generate questions from.
         * @param count        The number of questions to generate.
         * @return A list of generated Question objects.
         */
        public List<Question> generateQuestions(String syllabusText, int count) {
                if (geminiApiKey == null || geminiApiKey.isBlank()) {
                        logger.warn("Gemini API Key is missing. Falling back to Mock DB.");
                        return generateMockQuestions(count);
                }

                logger.info("Generating {} questions using PARALLEL BATCHES (Size: {})...", count, BATCH_SIZE);
                List<java.util.concurrent.CompletableFuture<List<Question>>> futures = new ArrayList<>();

                int questionsRemaining = count;
                while (questionsRemaining > 0) {
                        int currentBatchSize = Math.min(BATCH_SIZE, questionsRemaining);
                        questionsRemaining -= currentBatchSize;

                        final int batchCount = currentBatchSize;

                        futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                                try {
                                        return generateBatch(syllabusText, batchCount);
                                } catch (Exception e) {
                                        logger.error("Error generating batch of size {}", batchCount, e);
                                        return new ArrayList<>(); // Return empty on failure to avoid nulls
                                }
                        }));
                }

                List<Question> allQuestions = futures.stream()
                                .map(java.util.concurrent.CompletableFuture::join)
                                .flatMap(List::stream)
                                .collect(java.util.stream.Collectors.toList());

                if (allQuestions.isEmpty()) {
                        logger.error("All batch requests failed. Returning mock questions as fallback.");
                        return generateMockQuestions(count);
                }

                logger.info("Successfully generated {} questions.", allQuestions.size());
                return allQuestions;
        }

        private List<Question> generateBatch(String syllabusText, int count) throws Exception {
                // Construct the prompt for Gemini
                // We request a strict JSON Array format to ensure easy parsing.
                String prompt = "Generate EXACTLY " + count
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

                // Gemini API request format
                Map<String, Object> bodyMap = Map.of(
                                "contents", List.of(
                                                Map.of("parts", List.of(
                                                                Map.of("text", prompt)))),
                                "generationConfig", Map.of(
                                                "temperature", 0.7,
                                                "maxOutputTokens", 8192));

                String requestBody = mapper.writeValueAsString(bodyMap);

                // Add API key as query parameter
                String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                                .uri(java.net.URI.create(urlWithKey))
                                .header("Content-Type", "application/json")
                                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                java.net.http.HttpResponse<String> response = httpClient.send(request,
                                java.net.http.HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());

                        // Extract text from Gemini response format
                        String responseText = root.path("candidates").get(0)
                                        .path("content").path("parts").get(0)
                                        .path("text").asText();

                        // Log truncated response for debugging without spamming logs
                        if (logger.isDebugEnabled()) {
                                logger.debug("Raw Gemini Batch Response: {}...",
                                                responseText.substring(0, Math.min(responseText.length(), 200)));
                        }

                        // Cleanup markdown code blocks if present
                        responseText = responseText.replaceAll("```json", "").replaceAll("```", "").trim();

                        try {
                                return mapper.readValue(responseText,
                                                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                });
                        } catch (Exception e) {
                                // JSON REPAIR STRATEGY
                                // LLMs sometimes truncate JSON or add trailing commas.
                                // We attempt to fix common issues like missing closing brackets.
                                try {
                                        int lastClose = responseText.lastIndexOf("}");
                                        if (lastClose != -1) {
                                                String repaired = responseText.substring(0, lastClose + 1) + "]";
                                                if (repaired.contains(",]"))
                                                        repaired = repaired.replace(",]", "]");

                                                logger.warn("JSON Parse failed. Attempting repair...");
                                                return mapper.readValue(repaired,
                                                                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                                });
                                        }
                                } catch (Exception ignore) {
                                        // Repair failed, fall through to main error
                                }

                                logger.error("Failed to parse batch JSON: {}", e.getMessage());
                                return new ArrayList<>(); // Fail gracefully
                        }
                } else {
                        logger.error("Gemini API Error: Status={}, Body={}", response.statusCode(), response.body());
                        return new ArrayList<>();
                }
        }

        private List<Question> generateMockQuestions(int count) {
                List<Question> result = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                        result.add(MOCK_DB.get(i % MOCK_DB.size()));
                }
                return result;
        }
}
