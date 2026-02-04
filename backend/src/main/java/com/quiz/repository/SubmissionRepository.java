package com.quiz.repository;

import com.quiz.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findBySessionId(Long sessionId);

    boolean existsByStudentIdAndSessionId(Long studentId, Long sessionId);

    java.util.Optional<Submission> findByStudentIdAndSessionId(Long studentId, Long sessionId);
}
