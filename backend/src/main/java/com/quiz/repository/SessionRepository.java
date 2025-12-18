package com.quiz.repository;

import com.quiz.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    java.util.List<Session> findByActiveTrue();
}
