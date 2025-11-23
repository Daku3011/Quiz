package com.quiz.controller;

import com.quiz.model.Student;
import com.quiz.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentRepository studentRepo;

    public StudentController(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        System.out.println("Register request: " + body);
        String name = body.get("name"), enrollment = body.get("enrollment");
        if (name == null || enrollment == null) {
            System.out.println("Missing fields");
            return ResponseEntity.badRequest().body("Missing");
        }
        try {
            Student s = new Student();
            s.setName(name);
            s.setEnrollment(enrollment);
            studentRepo.save(s);
            System.out.println("Saved student: " + s.getId());
            return ResponseEntity.ok(Map.of("studentId", s.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
