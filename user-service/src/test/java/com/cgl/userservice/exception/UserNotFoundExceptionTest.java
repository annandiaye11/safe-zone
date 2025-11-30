package com.cgl.userservice.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Given
        String errorMessage = "User not found with id: 123";

        // When
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConstructorWithDifferentMessage() {
        // Given
        String errorMessage = "User with email user@example.com not found";

        // When
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Then
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void testConstructorWithNullMessage() {
        // Given
        String errorMessage = null;

        // When
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Then
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void testConstructorWithEmptyMessage() {
        // Given
        String errorMessage = "";

        // When
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        // Then
        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    void testExceptionCanBeThrown() {
        // When & Then
        assertThatThrownBy(() -> {
            throw new UserNotFoundException("User not found");
        })
                .isInstanceOf(UserNotFoundException.class)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void testExceptionInheritance() {
        // When
        UserNotFoundException exception = new UserNotFoundException("Test message");

        // Then - Verify it's a RuntimeException (unchecked exception)
        assertThat(exception).isInstanceOf(RuntimeException.class).isInstanceOf(Exception.class).isInstanceOf(Throwable.class);
    }

    @Test
    void testTypicalUsageScenario_UserIdNotFound() {
        // Given
        String userId = "user-123";

        // When & Then
        assertThatThrownBy(() -> {
            throw new UserNotFoundException("User not found with id: " + userId);
        })
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: user-123")
                .hasNoCause();
    }

    @Test
    void testTypicalUsageScenario_EmailNotFound() {
        // Given
        String email = "nonexistent@example.com";

        // When & Then
        assertThatThrownBy(() -> {
            throw new UserNotFoundException("User not found with email: " + email);
        })
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with email: nonexistent@example.com");
    }

    @Test
    void testExceptionHasNoCause() {
        // When
        UserNotFoundException exception = new UserNotFoundException("Test");

        // Then
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testExceptionStackTrace() {
        // When
        UserNotFoundException exception = new UserNotFoundException("Test error");

        // Then
        assertThat(exception.getStackTrace()).isNotEmpty();
    }

    @Test
    void testMultipleInstances() {
        // When
        UserNotFoundException exception1 = new UserNotFoundException("Error 1");
        UserNotFoundException exception2 = new UserNotFoundException("Error 2");

        // Then - Each exception is independent
        assertThat(exception1).isNotSameAs(exception2);
        assertThat(exception1.getMessage()).isNotEqualTo(exception2.getMessage());
    }

    @Test
    void testLongErrorMessage() {
        // Given
        String longMessage = "User not found with the following criteria: id=123, email=user@example.com, name=John Doe, role=ADMIN";

        // When
        UserNotFoundException exception = new UserNotFoundException(longMessage);

        // Then
        assertThat(exception.getMessage()).isEqualTo(longMessage);
        assertThat(exception.getMessage().length()).isGreaterThan(50);
    }

    @Test
    void testExceptionToString() {
        // Given
        String message = "User not found";

        // When
        UserNotFoundException exception = new UserNotFoundException(message);

        // Then
        assertThat(exception.toString()).contains("UserNotFoundException");
        assertThat(exception.toString()).contains("User not found");
    }

    @Test
    void testMessageWithSpecialCharacters() {
        // Given
        String message = "User not found: @#$%^&*()!";

        // When
        UserNotFoundException exception = new UserNotFoundException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}

