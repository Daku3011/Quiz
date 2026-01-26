package com.quiz.service;

import com.quiz.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This service is the "brain" of our question generation. 
// it talks to the Google so for pdf upload we have already formated pdf from which model has to fetch Teaching and Examination Scheme: for total cradite then from that you will have total hours and last is course outcome table from that 3 thing generate the qurstion based on them  AI to turn syllabus text into actual quiz questions.
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
                q.setExplanation("Basic concept.");
                q.setChapter("General Engineering");
                MOCK_DB.add(q);
        }

        @org.springframework.beans.factory.annotation.Value("${gemini.api.key:#{null}}")
        private String geminiApiKey;

        @org.springframework.beans.factory.annotation.Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemma-3-27b-it:generateContent}")
        private String geminiApiUrl;

        @org.springframework.beans.factory.annotation.Value("${gemini.model:gemma-3-27b-it}")
        private String geminiModel;

        @org.springframework.beans.factory.annotation.Value("${glm.api.key:#{null}}")
        private String glmApiKey;

        @org.springframework.beans.factory.annotation.Value("${glm.api.url:#{null}}")
        private String glmApiUrl;

        @org.springframework.beans.factory.annotation.Value("${glm.model:#{null}}")
        private String glmModel;

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
                return generateQuestions(syllabusText, null, null, count, null);
        }

        public List<Question> generateQuestions(String syllabusText, String fileData, String mimeType, int count) {
                return generateQuestions(syllabusText, fileData, mimeType, count, null);
        }

        public List<Question> generateQuestions(String syllabusText, String fileData, String mimeType, int count,
                        List<Map<String, Object>> weights) {
                // Priority: Gemini > GLM > Mock
                if (geminiApiKey != null && !geminiApiKey.isBlank()) {
                        logger.info("Using Gemini AI for question generation. Model: {}", geminiModel);

                        // Check if model supports multimodal (Gemma does not support inlineData in this
                        // API version)
                        boolean isGemma = geminiModel != null && geminiModel.toLowerCase().contains("gemma");

                        if (!isGemma && fileData != null && !fileData.isBlank()) {
                                return generateMultimodalGemini(syllabusText, fileData, mimeType, count, weights);
                        }

                        if (isGemma && fileData != null && !fileData.isBlank()) {
                                logger.warn("Multimodal input provided but model is '{}'. Ignoring file/image data and using text-only.",
                                                geminiModel);
                        }

                        return generateQuestionsInternal(syllabusText, count, false, weights);
                }

                if (glmApiKey != null && !glmApiKey.isBlank() && (fileData == null || fileData.isBlank())) {
                        logger.info("Using GLM AI for question generation.");
                        return generateQuestionsInternal(syllabusText, count, true, weights);
                }

                logger.warn("No suitable AI API Keys found or Multimodal requested without Gemini. Falling back to Mock DB.");
                return generateMockQuestions(count);
        }

        public Map<String, Object> analyzeSyllabus(String syllabusText) {
                if (geminiApiKey == null || geminiApiKey.isBlank()) {
                        return Map.of("error", "AI API Key not configured");
                }

                try {
                        String prompt = "You are an expert curriculum analyst. Analyze the following syllabus/course document and: \n"
                                        +
                                        "1. Identify the 'Teaching and Examination Scheme' (Total Credits, Total Hours).\n"
                                        +
                                        "2. Extract a list of all Chapters/Units (Keep titles BRIEF and CONCISE, max 10 words).\n"
                                        +
                                        "3. Assign a suggested weight (percentage) to each chapter based on its technical depth and expected hours.\n\n"
                                        +
                                        "Return the result as a STRICT JSON OBJECT with this format:\n" +
                                        "{\n" +
                                        "  \"credits\": \"...\",\n" +
                                        "  \"hours\": \"...\",\n" +
                                        "  \"chapters\": [\n" +
                                        "    {\"name\": \"Chapter 1: ...\", \"weight\": 20, \"mappedCO\": \"CO1\"},\n" +
                                        "    ...\n" +
                                        "  ]\n" +
                                        "}\n" +
                                        "Ensure weights sum to 100. If total hours are not clearly stated, estimate based on credits. Extract the mapped Course Outcome (CO) for each chapter if available in the text, otherwise infer the most likely CO (e.g. CO1, CO2).\n\n"
                                        +
                                        "Syllabus Content:\n" + syllabusText;

                        Map<String, Object> bodyMap = Map.of(
                                        "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                                        "generationConfig", Map.of("temperature", 0.5, "maxOutputTokens", 2048));

                        String requestBody = mapper.writeValueAsString(bodyMap);
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
                                String responseText = root.path("candidates").get(0).path("content").path("parts")
                                                .get(0).path("text").asText();
                                String cleanedJson = responseText.replace("```json", "").replace("```", "").trim();
                                return mapper.readValue(cleanedJson,
                                                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                                                });
                        }
                } catch (Exception e) {
                        logger.error("Failed to analyze syllabus", e);
                }
                return Map.of("error", "Analysis failed");
        }

        private List<Question> generateMultimodalGemini(String text, String fileData, String mimeType, int count,
                        List<Map<String, Object>> weights) {
                try {
                        logger.info("Sending Multimodal Request to Gemini (Size: {} bytes, Type: {})",
                                        fileData.length(), mimeType);

                        String prompt = constructPrompt(text != null ? text : "Refer to the attached document.", count,
                                        weights);

                        // Gemini API request format with Inline Data
                        Map<String, Object> bodyMap = Map.of(
                                        "contents", List.of(
                                                        Map.of("parts", List.of(
                                                                        Map.of("text", prompt),
                                                                        Map.of("inlineData", Map.of(
                                                                                        "mimeType", mimeType,
                                                                                        "data", fileData))))),
                                        "generationConfig", Map.of(
                                                        "temperature", 0.7,
                                                        "maxOutputTokens", 8192));

                        String requestBody = mapper.writeValueAsString(bodyMap);
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
                                com.fasterxml.jackson.databind.JsonNode candidate = root.path("candidates").get(0);

                                if (candidate.isMissingNode() || candidate.path("content").isMissingNode()) {
                                        logger.error("Gemini returned no candidates: {}", response.body());
                                        return new ArrayList<>();
                                }

                                String responseText = candidate.path("content").path("parts").get(0).path("text")
                                                .asText();
                                String cleanedJson = extractJsonArray(responseText);

                                return mapper.readValue(cleanedJson,
                                                new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                });
                        } else {
                                logger.error("Gemini Multimodal Error: Status={}, Body={}", response.statusCode(),
                                                response.body());
                        }

                } catch (Exception e) {
                        logger.error("Failed to generate multimodal questions", e);
                }
                return new ArrayList<>();
        }

        // Limit max questions per AI call to avoid token limits/hallucinations
        private static final int MAX_QUESTIONS_PER_REQUEST = 20;

        private List<Question> generateQuestionsInternal(String syllabusText, int count, boolean useGlm,
                        List<Map<String, Object>> weights) {
                List<String> chunks = splitString(syllabusText, CHUNK_SIZE);
                int totalChunks = chunks.size();

                logger.info("Split input text into {} chunks for processing.", totalChunks);

                List<java.util.concurrent.CompletableFuture<List<Question>>> futures = new ArrayList<>();

                int baseQ = count / totalChunks;
                int remainder = count % totalChunks;

                for (int i = 0; i < totalChunks; i++) {
                        // Distribute questions evenly to text chunks
                        int qForThisChunk = baseQ + (i < remainder ? 1 : 0);

                        if (qForThisChunk > 0) {
                                String chunkText = chunks.get(i);

                                // Further split if this chunk requires too many questions
                                int subBatches = (int) Math.ceil((double) qForThisChunk / MAX_QUESTIONS_PER_REQUEST);
                                int questionsPerSubBatch = qForThisChunk / subBatches;
                                int subRemainder = qForThisChunk % subBatches;

                                for (int j = 0; j < subBatches; j++) {
                                        final int batchCount = questionsPerSubBatch + (j < subRemainder ? 1 : 0);

                                        futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                                                try {
                                                        if (useGlm) {
                                                                return generateBatchGLM(chunkText, batchCount, weights);
                                                        } else {
                                                                return generateBatch(chunkText, batchCount, weights);
                                                        }
                                                } catch (Exception e) {
                                                        logger.error("Error generating batch for chunk", e);
                                                        return new ArrayList<>();
                                                }
                                        }));
                                }
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

        // Helper method to construct the prompt string.
        private String constructPrompt(String syllabusText, int count, List<Map<String, Object>> weights) {
                StringBuilder weightConstraint = new StringBuilder();
                if (weights != null && !weights.isEmpty()) {
                        weightConstraint.append("\nDISTRIBUTE QUESTIONS ACCORDING TO THESE CHAPTER WEIGHTS:\n");
                        for (Map<String, Object> w : weights) {
                                weightConstraint.append("- ").append(w.get("name")).append(": ")
                                                .append(w.get("weight")).append("% of questions.");
                                if (w.containsKey("mappedCO") && w.get("mappedCO") != null) {
                                        weightConstraint.append(" (MUST BE TAGGED AS ").append(w.get("mappedCO"))
                                                        .append(")");
                                }
                                weightConstraint.append("\n");
                        }
                }

                return "You are an expert engineering instructor. Your task is to generate EXACTLY " + count
                                + " multiple-choice questions based on the technical content of the following syllabus/reference text.\n\n"
                                +
                                "PRIMARY GOAL: Generate high-quality technical questions about the TOPICS and CONCEPTS mentioned in the text (e.g., algorithms, code, engineering principles).\n\n"
                                +
                                "CORE INSTRUCTIONS:\n" +
                                "1. Locate the 'Teaching and Examination Scheme' only to understand the 'Total Credits' and 'Total Hours' for context.\n"
                                +
                                "2. Use the 'Course Outcome' (CO) table to align the difficulty of the questions.\n" +
                                "3. CRITICAL: DO NOT generate questions ABOUT the syllabus, credits, hours, or course outcomes (e.g., NO 'What is the syllabus?', NO 'How many hours are there?').\n"
                                +
                                "4. ALL questions must be technical and about the ACTUAL SUBJECT MATTER (e.g., 'In Java, what is JVM?').\n"
                                +
                                "5. If this chunk of text contains mostly metadata/tables and very little technical content, try to find the nearest chapter title or topic and generate questions about THAT topic instead of the metadata.\n\n"
                                +
                                "CONSTRAINTS:\n" +
                                weightConstraint.toString() +
                                "- The questions MUST be strictly mapped to the identified Course Outcomes (CO1, CO2, etc.) in their technical depth.\n"
                                +
                                "- Return the response as a valid JSON ARRAY of objects.\n\n" +
                                "JSON Format:\n" +
                                "[\n" +
                                "  {\"text\": \"...\", \"optionA\": \"...\", \"optionB\": \"...\", \"optionC\": \"...\", \"optionD\": \"...\", \"correct\": \"A\", \"explanation\": \"...\", \"chapter\": \"Chapter Name\", \"courseOutcome\": \"CO1\"},\n"
                                +
                                "  ... (total " + count + " objects)\n" +
                                "]\n\n" +
                                "CRITICAL REQUIREMENTS:\n" +
                                "1. Output MUST be purely valid JSON. No markdown formatting, no conversational text.\n"
                                +
                                "2. Escape all quotes and backslashes.\n" +
                                "3. Mix of difficulty: 30% Hard, 40% Medium, 30% Conceptual.\n" +
                                "4. INCLUDE CODE SNIPPETS in the 'text' field where applicable.\n" +
                                "5. 'explanation' must be concise (max 30 words).\n" +
                                "6. 'chapter' field must MATCH one of the chapter names provided in weights or a relevant topic name from the text.\n"
                                +
                                "7. 'courseOutcome' field is REQUIRED (e.g., 'CO1', 'CO2'). Map the question to the most relevant Course Outcome from the text.\n\n"
                                +
                                "Reference Text:\n" + syllabusText;
        }

        private List<Question> generateBatchGLM(String syllabusText, int count, List<Map<String, Object>> weights)
                        throws Exception {
                String prompt = constructPrompt(syllabusText, count, weights);

                // GLM (OpenAI-compatible) API request format
                Map<String, Object> bodyMap = Map.of(
                                "model", glmModel,
                                "messages", List.of(
                                                Map.of("role", "user", "content", prompt)),
                                "temperature", 0.7);

                String requestBody = mapper.writeValueAsString(bodyMap);

                int maxRetries = 3;
                int attempt = 0;

                while (attempt < maxRetries) {
                        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                                        .uri(java.net.URI.create(glmApiUrl))
                                        .header("Content-Type", "application/json")
                                        .header("Authorization", "Bearer " + glmApiKey)
                                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                                        .build();

                        java.net.http.HttpResponse<String> response = httpClient.send(request,
                                        java.net.http.HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());

                                // GLM/OpenAI response structure: choices[0].message.content
                                String responseText = root.path("choices").get(0)
                                                .path("message").path("content").asText();

                                if (logger.isDebugEnabled()) {
                                        logger.debug("Raw GLM Batch Response: {}...",
                                                        responseText.substring(0,
                                                                        Math.min(responseText.length(), 200)));
                                }

                                String cleanedJson = extractJsonArray(responseText);

                                try {
                                        return mapper.readValue(cleanedJson,
                                                        new com.fasterxml.jackson.core.type.TypeReference<List<Question>>() {
                                                        });
                                } catch (Exception e) {
                                        logger.error("Failed to parse GLM batch JSON: {}", e.getMessage());
                                        // Basic repair attempt
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
                                logger.warn("GLM 429 Rate Limit Hit. Sleeping 5s before retry {}/{}...", attempt,
                                                maxRetries);
                                try {
                                        Thread.sleep(5000);
                                } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        throw new Exception("Interrupted during rate limit backoff");
                                }
                        } else {
                                logger.error("GLM API Error: Status={}, Body={}", response.statusCode(),
                                                response.body());
                                return new ArrayList<>();
                        }
                }
                return new ArrayList<>();
        }

        // Here we handle the actual communication with Gemini for a single batch.
        // We prompt it to give us strict JSON back so we can easily turn it into
        // Question objects.
        private List<Question> generateBatch(String syllabusText, int count, List<Map<String, Object>> weights)
                        throws Exception {
                // Construct the prompt for Gemini
                String prompt = constructPrompt(syllabusText, count, weights);

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

                                        // Fallback: Regex-based extraction for partial recovery
                                        List<Question> recovered = new ArrayList<>();
                                        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\{[\\s\\S]*?\\}");
                                        java.util.regex.Matcher m = p.matcher(cleanedJson);
                                        while (m.find()) {
                                                String objStr = m.group();
                                                try {
                                                        Question q = mapper.readValue(objStr, Question.class);
                                                        // Basic validation
                                                        if (q.getText() != null && !q.getText().isBlank()) {
                                                                recovered.add(q);
                                                        }
                                                } catch (Exception ex) {
                                                        // Ignore individual malformed objects
                                                }
                                        }

                                        if (!recovered.isEmpty()) {
                                                logger.info("Recovered {} questions via regex fallback.",
                                                                recovered.size());
                                                return recovered;
                                        }

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
                // Fallback: return input if brackets not found (might be raw json without
                // markdown) or just cleanup markdown if no brackets found (unlikely for array)
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
