package com.quiz.model;

import jakarta.persistence.*;

/**
 * Represents a student registered for a specific session.
 * Stores enrollment details and links to the session.
 */
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String enrollment;

    /** The specific session ID this student is registered for. */
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
