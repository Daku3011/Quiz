package com.quiz.model;

import jakarta.persistence.*;

/**
 * Represents a multiple-choice question.
 * Contains the question text, four options, the correct answer key, and an
 * explanation.
 */
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String text;
    @Column(length = 2048)
    private String optionA;
    @Column(length = 2048)
    private String optionB;
    @Column(length = 2048)
    private String optionC;
    @Column(length = 2048)
    private String optionD;
    private String correct; // A/B/C/D

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

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    @Lob
    private String explanation;

    private String chapter;

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    private String courseOutcome; // e.g., CO1, CO2, etc.

    public String getCourseOutcome() {
        return courseOutcome;
    }

    public void setCourseOutcome(String courseOutcome) {
        this.courseOutcome = courseOutcome;
    }

    @Override
    public String toString() {
        return text + " [Chapter: " + chapter + "]\nA) " + optionA + " B) " + optionB + " C) " + optionC + " D) "
                + optionD;
    }
}
