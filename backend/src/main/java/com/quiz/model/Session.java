package com.quiz.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Represents a live quiz session.
 * Manages the timing, OTP access, and associated questions for an exam.
 */
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    /** Linked security details for the session OTP */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "otp_id", referencedColumnName = "id")
    private Otp otpDetails;

    private boolean active = true;

    private Instant createdAt = Instant.now();

    /** Exam start time; students cannot join before this. */
    private Instant startTime;

    /** Exam end time; late submissions might be rejected or flagged. */
    private Instant endTime;

    /** Number of question sets (e.g., 4 for Sets A, B, C, D) to deter cheating. */
    private int numberOfSets = 1; // Default 1 set

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(int numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public Otp getOtpDetails() {
        return otpDetails;
    }

    public void setOtpDetails(Otp otpDetails) {
        this.otpDetails = otpDetails;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    // Coding Exam support
    private Boolean coding = false;
    private String programmingLanguage;

    @Lob
    @Column(length = 10000)
    private String problemStatementEven;

    @Lob
    @Column(length = 10000)
    private String problemStatementOdd;

    public boolean isCoding() {
        return coding != null && coding;
    }

    public void setCoding(boolean coding) {
        this.coding = coding;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getProblemStatementEven() {
        return problemStatementEven;
    }

    public void setProblemStatementEven(String problemStatementEven) {
        this.problemStatementEven = problemStatementEven;
    }

    public String getProblemStatementOdd() {
        return problemStatementOdd;
    }

    public void setProblemStatementOdd(String problemStatementOdd) {
        this.problemStatementOdd = problemStatementOdd;
    }
}
