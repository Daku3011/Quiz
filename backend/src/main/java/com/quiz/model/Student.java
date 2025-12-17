package com.quiz.model;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String enrollment;
    private Long sessionId;

    // GETTERS
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public Long getSessionId() {
        return sessionId;
    }

    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
