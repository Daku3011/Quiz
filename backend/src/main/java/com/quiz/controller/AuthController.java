package com.quiz.controller;

import com.quiz.model.Faculty;
import com.quiz.repository.FacultyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final FacultyRepository facultyRepo;

    public AuthController(FacultyRepository facultyRepo) {
        this.facultyRepo = facultyRepo;
    }

    @PostMapping("/faculty/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");
        String displayName = body.getOrDefault("displayName", username);

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }

        // Optional check
        if (facultyRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        Faculty f = new Faculty();
        f.setUsername(username);
        f.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        f.setDisplayName(displayName);

        facultyRepo.save(f);

        return ResponseEntity.ok(Map.of("id", f.getId()));
    }

    @PostMapping("/faculty/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        var maybe = facultyRepo.findByUsername(username);

        if (maybe.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid username");
        }

        Faculty f = maybe.get();

        if (!BCrypt.checkpw(password, f.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        return ResponseEntity.ok(Map.of(
                "facultyId", f.getId(),
                "displayName", f.getDisplayName()
        ));
    }
}
