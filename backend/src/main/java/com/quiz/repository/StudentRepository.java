package com.quiz.repository;

import com.quiz.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEnrollmentAndSessionId(String enrollment, Long sessionId);
}
