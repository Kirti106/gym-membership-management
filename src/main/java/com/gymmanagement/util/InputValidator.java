package com.gymmanagement.util;

import com.gymmanagement.exception.EmptyFieldException;
import com.gymmanagement.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String validateNonEmpty(String input, String fieldName) throws EmptyFieldException {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyFieldException(fieldName + " cannot be empty or blank.");
        }
        return input.trim();
    }

    public static String validateEmail(String email) throws ValidationException, EmptyFieldException {
        String cleanEmail = validateNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new ValidationException("Invalid email format. Example: user@example.com");
        }
        return cleanEmail;
    }

    public static String validatePhone(String phone) throws ValidationException, EmptyFieldException {
        String cleanPhone = validateNonEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new ValidationException("Invalid phone number. Must contain 10-15 digits.");
        }
        return cleanPhone;
    }

    public static int validatePositiveInt(String input, String fieldName) throws ValidationException {
        try {
            int val = Integer.parseInt(input.trim());
            if (val <= 0) {
                throw new ValidationException(fieldName + " must be a positive integer.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid integer number.");
        }
    }

    public static int validateAge(String input) throws ValidationException {
        int age = validatePositiveInt(input, "Age");
        if (age < 12 || age > 100) {
            throw new ValidationException("Age must be between 12 and 100 years.");
        }
        return age;
    }

    public static double validatePositiveDouble(String input, String fieldName) throws ValidationException {
        try {
            double val = Double.parseDouble(input.trim());
            if (val <= 0) {
                throw new ValidationException(fieldName + " must be greater than zero.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid decimal number.");
        }
    }

    public static LocalDate validateDate(String input, String fieldName) throws ValidationException {
        try {
            return LocalDate.parse(input.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must follow format YYYY-MM-DD (e.g. 2026-03-25).");
        }
    }
}
