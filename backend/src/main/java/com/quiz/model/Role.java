package com.quiz.model;

/**
 * Defines the roles available in the system for authorization.
 */
public enum Role {
    /** Administrator with full system access */
    ADMIN,

    /** Faculty member who creates quizzes */
    FACULTY,

    /** Student who takes quizzes */
    STUDENT
}
