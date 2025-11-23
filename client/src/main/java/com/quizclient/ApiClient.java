package com.quizclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.*;
import java.net.URI;
import java.time.Duration;

public class ApiClient {
    // If your backend is not on localhost:8080, change this.
    // Read from system property "app.url" or default to localhost
    public static final String BASE_URL = System.getProperty("app.url", "http://localhost:8080");

    public static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static HttpRequest.Builder jsonRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(20));
    }
}
