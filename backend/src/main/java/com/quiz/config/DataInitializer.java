package com.quiz.config;

import com.quiz.model.Role;
import com.quiz.model.User;
import com.quiz.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This class takes care of setting up some basic data when the app starts up.
// Specifically, it makes sure there's always an admin account ready to go.
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            // We'll check if an "admin" user exists. If not, we create one with a default
            // password.
            // This is super helpful for the first time you run the app!
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN);
                userRepo.save(admin);
                System.out.println("Success: Created the default admin account (user: admin, pass: admin123)");
            }
        };
    }
}
