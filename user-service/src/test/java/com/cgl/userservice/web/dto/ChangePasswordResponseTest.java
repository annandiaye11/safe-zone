package com.cgl.userservice.web.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour ChangePasswordResponse DTO
 */
class ChangePasswordResponseTest {

    @Test
    void testNoArgsConstructor() {
        // When
        ChangePasswordResponse response = new ChangePasswordResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse(); // default boolean value
        assertThat(response.getMessage()).isNull();
        assertThat(response.getTimestamp()).isNull();
    }

    @Test
    void testTwoArgsConstructor() {
        // Given
        boolean success = true;
        String message = "Password changed successfully";

        // When
        ChangePasswordResponse response = new ChangePasswordResponse(success, message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Password changed successfully");
        assertThat(response.getTimestamp()).isNull(); // timestamp not set by this constructor
    }

    @Test
    void testTwoArgsConstructor_WithFailure() {
        // Given
        boolean success = false;
        String message = "Current password is incorrect";

        // When
        ChangePasswordResponse response = new ChangePasswordResponse(success, message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Current password is incorrect");
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        boolean success = true;
        String message = "Password updated";
        LocalDateTime timestamp = LocalDateTime.of(2025, 11, 30, 14, 30, 0);

        // When
        ChangePasswordResponse response = new ChangePasswordResponse(success, message, timestamp);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Password updated");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void testSetters() {
        // Given
        ChangePasswordResponse response = new ChangePasswordResponse();
        LocalDateTime now = LocalDateTime.now();

        // When
        response.setSuccess(true);
        response.setMessage("Password changed");
        response.setTimestamp(now);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Password changed");
        assertThat(response.getTimestamp()).isEqualTo(now);
    }

    @Test
    void testGetters() {
        // Given
        LocalDateTime timestamp = LocalDateTime.of(2025, 11, 30, 12, 0, 0);
        ChangePasswordResponse response = new ChangePasswordResponse(true, "Success", timestamp);

        // When & Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void testSetSuccess_ToggleValue() {
        // Given
        ChangePasswordResponse response = new ChangePasswordResponse(true, "Initial");

        // When - Toggle to false
        response.setSuccess(false);

        // Then
        assertThat(response.isSuccess()).isFalse();

        // When - Toggle back to true
        response.setSuccess(true);

        // Then
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void testSetMessage_UpdateValue() {
        // Given
        ChangePasswordResponse response = new ChangePasswordResponse(false, "Initial message");

        // When
        response.setMessage("Updated message");

        // Then
        assertThat(response.getMessage()).isEqualTo("Updated message");
    }

    @Test
    void testSetTimestamp_UpdateValue() {
        // Given
        LocalDateTime initial = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2025, 11, 30, 15, 0);
        ChangePasswordResponse response = new ChangePasswordResponse(true, "Message", initial);

        // When
        response.setTimestamp(updated);

        // Then
        assertThat(response.getTimestamp()).isEqualTo(updated);
    }

    @Test
    void testResponseWithNullMessage() {
        // Given & When
        ChangePasswordResponse response = new ChangePasswordResponse(true, null);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testResponseWithEmptyMessage() {
        // Given & When
        ChangePasswordResponse response = new ChangePasswordResponse(false, "");

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEmpty();
    }

    @Test
    void testResponseWithLongMessage() {
        // Given
        String longMessage = "This is a very long error message that contains detailed information " +
                "about why the password change failed and what the user should do next";

        // When
        ChangePasswordResponse response = new ChangePasswordResponse(false, longMessage);

        // Then
        assertThat(response.getMessage()).isEqualTo(longMessage);
        assertThat(response.getMessage().length()).isGreaterThan(100);
    }

    @Test
    void testTimestampWithCurrentTime() {
        // Given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // When
        ChangePasswordResponse response = new ChangePasswordResponse();
        response.setSuccess(true);
        response.setMessage("Password changed");
        response.setTimestamp(now);

        // Then
        assertThat(response.getTimestamp()).isAfterOrEqualTo(before);
        assertThat(response.getTimestamp()).isBeforeOrEqualTo(after);
    }

    @Test
    void testSuccessResponse_TypicalScenario() {
        // Given - Typical success response
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ChangePasswordResponse response = new ChangePasswordResponse(
                true,
                "Your password has been successfully changed",
                timestamp
        );

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).contains("successfully");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void testFailureResponse_TypicalScenario() {
        // Given - Typical failure response
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ChangePasswordResponse response = new ChangePasswordResponse(
                false,
                "Current password is incorrect",
                timestamp
        );

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("incorrect");
        assertThat(response.getTimestamp()).isNotNull();
    }
}

