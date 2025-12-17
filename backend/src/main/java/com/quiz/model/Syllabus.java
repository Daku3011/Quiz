package com.quiz.model;

import jakarta.persistence.*;

/**
 * Stores raw text content used for generating questions.
 * Useful for auditing what content was used for a particular quiz generation.
 */
@Entity
public class Syllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
