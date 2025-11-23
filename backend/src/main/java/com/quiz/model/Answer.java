package com.quiz.model;

import jakarta.persistence.*;

@Entity
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questionId;
    private Long studentId;
    private String selectedOption; // "A"/"B"/"C"/"D"
    private boolean correct;
    // getters/setters
}
