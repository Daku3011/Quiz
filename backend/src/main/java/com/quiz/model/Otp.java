package com.quiz.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otps")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiryTime;

    private int attempts = 0;

    private static final int MAX_ATTEMPTS = 5;

    @OneToOne(mappedBy = "otpDetails")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Session session;

    public Otp() {
    }

    public Otp(String codeHash, Instant expiryTime) {
        this.codeHash = codeHash;
        this.expiryTime = expiryTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public boolean isBlocked() {
        return attempts >= MAX_ATTEMPTS;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryTime);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
