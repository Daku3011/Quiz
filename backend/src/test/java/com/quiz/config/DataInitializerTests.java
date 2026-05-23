package com.quiz.config;

import com.quiz.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DataInitializerTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void adminUserIsSeededOnStartup() {
        assertTrue(userRepository.findByUsername("admin").isPresent());
    }
}
