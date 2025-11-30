package com.cgl.userservice.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvalidPasswordExceptionTest {

    @Test
    void testConstructor_WithMessage() {
        // Given
        String errorMessage = "Password is invalid";

        // When
        InvalidPasswordException exception = new InvalidPasswordException(errorMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Password is invalid");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testException_CanBeThrown() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new InvalidPasswordException("Password must contain at least 8 characters");
        })
        .isInstanceOf(InvalidPasswordException.class)
        .hasMessage("Password must contain at least 8 characters");
    }

    @Test
    void testException_WithDifferentMessages() {
        // Test with different error messages
        InvalidPasswordException ex1 = new InvalidPasswordException("Password too short");
        InvalidPasswordException ex2 = new InvalidPasswordException("Password too weak");
        InvalidPasswordException ex3 = new InvalidPasswordException("Password must contain special characters");

        assertThat(ex1.getMessage()).isEqualTo("Password too short");
        assertThat(ex2.getMessage()).isEqualTo("Password too weak");
        assertThat(ex3.getMessage()).isEqualTo("Password must contain special characters");
    }

    @Test
    void testException_WithNullMessage() {
        // When
        InvalidPasswordException exception = new InvalidPasswordException(null);

        // Then
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void testException_WithEmptyMessage() {
        // When
        InvalidPasswordException exception = new InvalidPasswordException("");

        // Then
        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    void testException_IsRuntimeException() {
        // Given
        InvalidPasswordException exception = new InvalidPasswordException("Test");

        // Then - Verify it's unchecked (RuntimeException)
        assertThat(exception).isInstanceOf(RuntimeException.class).isInstanceOf(Exception.class).isInstanceOf(Throwable.class);
    }

    @Test
    void testException_CanBeCaught() {
        // Given
        String errorMessage = "Invalid password format";

        try {
            // When
            throw new InvalidPasswordException(errorMessage);
        } catch (InvalidPasswordException e) {
            // Then
            assertThat(e.getMessage()).isEqualTo(errorMessage);
        }
    }

    @Test
    void testException_WithLongMessage() {
        // Given
        String longMessage = "The password you provided does not meet the security requirements: " +
                "it must be at least 8 characters long, contain at least one uppercase letter, " +
                "one lowercase letter, one digit, and one special character.";

        // When
        InvalidPasswordException exception = new InvalidPasswordException(longMessage);

        // Then
        assertThat(exception.getMessage()).isEqualTo(longMessage);
        assertThat(exception.getMessage().length()).isGreaterThan(100);
    }

    @Test
    void testException_Stacktrace() {
        // Given
        InvalidPasswordException exception = new InvalidPasswordException("Test exception");

        // When
        StackTraceElement[] stackTrace = exception.getStackTrace();

        // Then
        assertThat(stackTrace).isNotNull().hasSizeGreaterThan(0);
    }

    @Test
    void testException_ToString() {
        // Given
        String message = "Password validation failed";
        InvalidPasswordException exception = new InvalidPasswordException(message);

        // When
        String result = exception.toString();

        // Then
        assertThat(result).contains("InvalidPasswordException").contains(message);
    }

    @Test
    void testException_TypicalUsageScenario() {
        // Simulate typical usage in password validation
        String password = "weak";

        // When & Then
        assertThatThrownBy(() -> validatePassword(password))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("too short");
    }

    // Helper method to simulate typical usage
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new InvalidPasswordException("Password is too short");
        }
    }

    @Test
    void testException_InheritedMethods() {
        // Given
        InvalidPasswordException exception = new InvalidPasswordException("Test");

        // Then - Test inherited methods from RuntimeException
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getLocalizedMessage()).isEqualTo("Test");
        assertThat(exception.getSuppressed()).isEmpty();
    }
}

