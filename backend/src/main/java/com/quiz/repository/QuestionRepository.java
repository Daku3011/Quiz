package com.quiz.repository;

import com.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
