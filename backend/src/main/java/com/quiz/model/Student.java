package com.quiz.model;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String enrollment;

    // GETTERS
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEnrollment() { return enrollment; }

    // SETTERS
    public void setName(String name) { this.name = name; }
    public void setEnrollment(String enrollment) { this.enrollment = enrollment; }
}
