package com.quiz.config;

import com.quiz.model.Role;
import com.quiz.model.User;
import com.quiz.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds the database with initial data on startup.
 * Creates a default Admin user if none exists.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin exists
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN);
                userRepo.save(admin);
                System.out.println("Default Admin created: admin / admin123");
            }
        };
    }
}
