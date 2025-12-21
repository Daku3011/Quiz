package com.quiz.service;

import com.quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This service is the "brain" of our question generation. 
// it talks to the Google Gemini AI to turn syllabus text into actual quiz questions.
@Service
public class AIService {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AIService.class);

        // We keep a small "mock database" here just in case the AI is unavailable.
        // It's full of general engineering questions to keep things running during
        // development.
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
        private final com.fasterxml.jackson.databind.ObjectMapper mapper = com.fasterxml.jackson.databind.json.JsonMapper
                        .builder()
                        .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
                        .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_JAVA_COMMENTS)
                        .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
                        .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_SINGLE_QUOTES)
                        .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                        .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
                        .build();

        // Batch size for parallel requests. Increased to 10 for higher throughput.
        private static final int BATCH_SIZE = 10;

        // This is the main entry point to generate questions.
        // If we have an AI key, we'll use Gemini in parallel batches to speed things
        // up.
        // If not, we fall back to our mock questions so the app doesn't break.
        // Chunk size for large files (approx 15k chars to stay safely within token
        // limits detailed context)
        private static final int CHUNK_SIZE = 15000;

        public List<Question> generateQuestions(String syllabusText, int count) {
                if (geminiApiKey == null || geminiApiKey.isBlank()) {
                        logger.warn("Gemini API Key is missing. Falling back to Mock DB.");
                        return generateMockQuestions(count);
                }

                List<String> chunks = splitString(syllabusText, CHUNK_SIZE);
                int totalChunks = chunks.size();

                logger.info("Split input text into {} chunks for processing.", totalChunks);

                List<java.util.concurrent.CompletableFuture<List<Question>>> futures = new ArrayList<>();

                int baseQ = count / totalChunks;
                int remainder = count % totalChunks;

                for (int i = 0; i < totalChunks; i++) {
                        // Distribute questions evenly
                        int qForThisChunk = baseQ + (i < remainder ? 1 : 0);

                        if (qForThisChunk > 0) {
                                String chunkText = chunks.get(i);
                                final int currentBatchCount = qForThisChunk;

                                futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                                        try {
                                                return generateBatch(chunkText, currentBatchCount);
                                        } catch (Exception e) {
                                                logger.error("Error generating batch for chunk", e);
                                                return new ArrayList<>();
                                        }
                                }));
                        }
                }

                List<Question> allQuestions = futures.stream()
                                .map(java.util.concurrent.CompletableFuture::join)
                                .flatMap(List::stream)
                                .collect(java.util.stream.Collectors.toList());

                if (allQuestions.isEmpty()) {
                        logger.error("All batch requests failed. Returning mock questions as fallback.");
                        return generateMockQuestions(count);
                }

                logger.info("Successfully generated {} questions from {} chunks.", allQuestions.size(), totalChunks);
                return allQuestions;
        }

        private List<String> splitString(String text, int maxSize) {
                List<String> res = new ArrayList<>();
                int len = text.length();
                for (int i = 0; i < len; i += maxSize) {
                        res.add(text.substring(i, Math.min(len, i + maxSize)));
                }
                return res;
        }

        // Here we handle the actual communication with Gemini for a single batch.
        // We prompt it to give us strict JSON back so we can easily turn it into
        // Question objects.
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

                int maxRetries = 3;
                int attempt = 0;

                while (attempt < maxRetries) {
                        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                                        .uri(java.net.URI.create(urlWithKey))
                                        .header("Content-Type", "application/json")
                                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                                        .build();

                        java.net.http.HttpResponse<String> response = httpClient.send(request,
                                        java.net.http.HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                                // ... existing success logic ...
                                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());
                                String responseText = root.path("candidates").get(0)
                                                .path("content").path("parts").get(0)
                                                .path("text").asText();

                                if (logger.isDebugEnabled()) {
                                        logger.debug("Raw Gemini Batch Response: {}...",
                                                        responseText.substring(0,
                                                                        Math.min(responseText.length(), 200)));
                                }

                                String cleanedJson = extractJsonArray(responseText);

                                try {
                                        return mapper.readValue(cleanedJson,
                                                        new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                        });
                                } catch (Exception e) {
                                        logger.error("Failed to parse batch JSON: {}", e.getMessage());
                                        // Try one more repair if it failed: adding closing bracket if missing
                                        if (e.getMessage().contains("Unexpected end-of-input")) {
                                                try {
                                                        return mapper.readValue(cleanedJson + "]",
                                                                        new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                                        });
                                                } catch (Exception ex) {
                                                        logger.error("Second attempt failed: {}", ex.getMessage());
                                                }
                                        }
                                        return new ArrayList<>();
                                }
                        } else if (response.statusCode() == 429) {
                                attempt++;
                                logger.warn("Gemini 429 Rate Limit Hit. Sleeping 25s before retry {}/{}...", attempt,
                                                maxRetries);
                                try {
                                        Thread.sleep(25000); // Wait 25s based on typical error message advice
                                } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        throw new Exception("Interrupted during rate limit backoff");
                                }
                        } else {
                                logger.error("Gemini API Error: Status={}, Body={}", response.statusCode(),
                                                response.body());
                                return new ArrayList<>(); // Non-retriable error
                        }
                }
                logger.error("Max retries exceeded for Gemini API.");
                return new ArrayList<>();
        }

        private String extractJsonArray(String input) {
                if (input == null)
                        return "[]";
                int start = input.indexOf('[');
                int end = input.lastIndexOf(']');
                if (start != -1 && end != -1 && end > start) {
                        return input.substring(start, end + 1);
                }
                // Fallback: return input if brackets not found (might be raw json without markdown)
                // or just cleanup markdown if no brackets found (unlikely for array) 
                return input.replace("```json", "").replace("```", "").trim();
        }

        private List<Question> generateMockQuestions(int count) {
                List<Question> result = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                        result.add(MOCK_DB.get(i % MOCK_DB.size()));
                }
                return result;
        }
}
