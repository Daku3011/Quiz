package com.quiz.controller;

import com.quiz.model.User;
import com.quiz.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

// This is the main gatekeeper for the app. 
// It handles the login logic for both Admins and Faculty members.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final com.quiz.repository.UserRepository userRepo;
    private final com.quiz.config.JwtUtils jwtUtils;
    private final com.quiz.service.TokenBlacklistService blacklistService;

    public AuthController(org.springframework.security.authentication.AuthenticationManager authenticationManager,
            com.quiz.repository.UserRepository userRepo,
            com.quiz.config.JwtUtils jwtUtils,
            com.quiz.service.TokenBlacklistService blacklistService) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username,
                            password));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid credentials"));
        }

        var user = userRepo.findByUsername(username).orElseThrow();
        var accessToken = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "role", user.getRole(),
                "username", user.getUsername(),
                "accessToken", accessToken,
                "refreshToken", refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String username = jwtUtils.extractUsername(refreshToken);
        if (username != null) {
            var user = userRepo.findByUsername(username).orElseThrow();
            if (jwtUtils.isTokenValid(refreshToken, user)) {
                var accessToken = jwtUtils.generateToken(user);
                return ResponseEntity.ok(Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken));
            }
        }
        return ResponseEntity.status(403).body("Invalid refresh token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.blacklistToken(token);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
