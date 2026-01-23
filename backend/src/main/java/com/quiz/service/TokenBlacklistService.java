package com.quiz.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to manage blacklisted JWT tokens.
 * In a production environment, this should ideally use Redis or a database
 * with a TTL (Time To Live) equal to the JWT expiration.
 * For this implementation, we use an in-memory Set.
 */
@Service
public class TokenBlacklistService {

    // Thread-safe set to store blacklisted tokens
    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());

    /**
     * Adds a token to the blacklist.
     * 
     * @param token The JWT token to invalidate.
     */
    public void blacklistToken(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedTokens.add(token);
        }
    }

    /**
     * Checks if a token is blacklisted.
     * 
     * @param token The JWT token to check.
     * @return true if the token is blacklisted, false otherwise.
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
