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

    private final UserRepository userRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // The login method checks the provided username and password against the
    // database.
    // If everything matches, it sends back the user's role and some basic info.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepo.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "role", user.getRole(),
                        "username", user.getUsername()));
            }
        }
        return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid credentials"));
    }
}
