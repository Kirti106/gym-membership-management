package com.gymmanagement;

import com.gymmanagement.exception.EmptyFieldException;
import com.gymmanagement.exception.ValidationException;
import com.gymmanagement.util.InputValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    @Test
    public void testValidEmail() throws Exception {
        String email = "john.doe@example.com";
        assertEquals("john.doe@example.com", InputValidator.validateEmail(email));
    }

    @Test
    public void testInvalidEmailThrowsException() {
        assertThrows(ValidationException.class, () -> InputValidator.validateEmail("invalid-email-string"));
    }

    @Test
    public void testValidPhone() throws Exception {
        assertEquals("9876543210", InputValidator.validatePhone("9876543210"));
    }

    @Test
    public void testInvalidPhoneThrowsException() {
        assertThrows(ValidationException.class, () -> InputValidator.validatePhone("123"));
    }

    @Test
    public void testEmptyFieldThrowsException() {
        assertThrows(EmptyFieldException.class, () -> InputValidator.validateNonEmpty("   ", "Field"));
    }

    @Test
    public void testPositiveIntValidation() throws Exception {
        assertEquals(25, InputValidator.validatePositiveInt("25", "Age"));
        assertThrows(ValidationException.class, () -> InputValidator.validatePositiveInt("-5", "Age"));
    }

    @Test
    public void testDateValidation() throws Exception {
        LocalDate date = InputValidator.validateDate("2026-05-15", "Date");
        assertEquals(2026, date.getYear());
        assertEquals(5, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());

        assertThrows(ValidationException.class, () -> InputValidator.validateDate("15-05-2026", "Date"));
    }
}
