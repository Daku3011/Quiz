package com.quiz.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    // At least one upper, one lower, one number, one special char
    private static final Pattern COMPLEXITY_PATTERN = Pattern
            .compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    public boolean isValid(String password) {
        if (password == null)
            return false;
        return COMPLEXITY_PATTERN.matcher(password).matches();
    }

    public String getPasswordRequirements() {
        return "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character.";
    }
}
