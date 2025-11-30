package com.cgl.userservice.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CheckTokenResponseTest {

    @Test
    void testNoArgsConstructor() {
        // When
        CheckTokenResponse response = new CheckTokenResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse(); // default boolean value
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        boolean valid = true;
        String message = "Token is valid";

        // When
        CheckTokenResponse response = new CheckTokenResponse(valid, message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Token is valid");
    }

    @Test
    void testAllArgsConstructor_WithFalse() {
        // Given
        boolean valid = false;
        String message = "Token expired";

        // When
        CheckTokenResponse response = new CheckTokenResponse(valid, message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Token expired");
    }

    @Test
    void testOneArgConstructor_WithValidToken() {
        // Given
        boolean valid = true;

        // When
        CheckTokenResponse response = new CheckTokenResponse(valid);

        // Then - Condition 1: valid == true → message = "Valid token"
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Valid token");
    }

    @Test
    void testOneArgConstructor_WithInvalidToken() {
        // Given
        boolean valid = false;

        // When
        CheckTokenResponse response = new CheckTokenResponse(valid);

        // Then - Condition 2: valid == false → message = "Invalid token"
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid token");
    }

    @Test
    void testBuilder_Success() {
        // When
        CheckTokenResponse response = CheckTokenResponse.builder()
                .valid(true)
                .message("Custom message")
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Custom message");
    }

    @Test
    void testBuilder_OnlyValid() {
        // When
        CheckTokenResponse response = CheckTokenResponse.builder()
                .valid(false)
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isNull(); // message not set by builder
    }

    @Test
    void testSetters() {
        // Given
        CheckTokenResponse response = new CheckTokenResponse();

        // When
        response.setValid(true);
        response.setMessage("Token validated");

        // Then
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Token validated");
    }

    @Test
    void testGetters() {
        // Given
        CheckTokenResponse response = new CheckTokenResponse(true, "Success");

        // When & Then
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success");
    }

    @Test
    void testSetValid_Toggle() {
        // Given
        CheckTokenResponse response = new CheckTokenResponse(true);

        // When - Toggle to false
        response.setValid(false);

        // Then
        assertThat(response.isValid()).isFalse();

        // When - Toggle back to true
        response.setValid(true);

        // Then
        assertThat(response.isValid()).isTrue();
    }

    @Test
    void testSetMessage_UpdateValue() {
        // Given
        CheckTokenResponse response = new CheckTokenResponse(true, "Initial");

        // When
        response.setMessage("Updated message");

        // Then
        assertThat(response.getMessage()).isEqualTo("Updated message");
    }

    @Test
    void testOneArgConstructor_ValidToken_AutoMessage() {
        // When - Testing the ternary operator: valid ? "Valid token" : "Invalid token"
        CheckTokenResponse validResponse = new CheckTokenResponse(true);

        // Then
        assertThat(validResponse.getMessage()).isEqualTo("Valid token");
        assertThat(validResponse.getMessage()).isNotEqualTo("Invalid token");
    }

    @Test
    void testOneArgConstructor_InvalidToken_AutoMessage() {
        // When - Testing the ternary operator: valid ? "Valid token" : "Invalid token"
        CheckTokenResponse invalidResponse = new CheckTokenResponse(false);

        // Then
        assertThat(invalidResponse.getMessage()).isEqualTo("Invalid token");
        assertThat(invalidResponse.getMessage()).isNotEqualTo("Valid token");
    }

    @Test
    void testBuilder_AllFields() {
        // When
        CheckTokenResponse response = CheckTokenResponse.builder()
                .valid(true)
                .message("JWT token is valid and not expired")
                .build();

        // Then
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).contains("valid");
        assertThat(response.getMessage()).contains("not expired");
    }

    @Test
    void testTypicalSuccessResponse() {
        // Given - Typical success scenario
        CheckTokenResponse response = new CheckTokenResponse(true);

        // Then
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Valid token");
    }

    @Test
    void testTypicalFailureResponse() {
        // Given - Typical failure scenario
        CheckTokenResponse response = new CheckTokenResponse(false);

        // Then
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid token");
    }

    @Test
    void testResponseWithNullMessage() {
        // Given
        CheckTokenResponse response = new CheckTokenResponse(true, null);

        // Then
        assertThat(response.isValid()).isTrue();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testResponseWithEmptyMessage() {
        // Given
        CheckTokenResponse response = new CheckTokenResponse(false, "");

        // Then
        assertThat(response.isValid()).isFalse();
        assertThat(response.getMessage()).isEmpty();
    }

    @Test
    void testTernaryOperator_BothBranches() {
        // Test both branches of: valid ? "Valid token" : "Invalid token"

        // Branch 1: valid = true
        CheckTokenResponse validCase = new CheckTokenResponse(true);
        assertThat(validCase.getMessage()).isEqualTo("Valid token");

        // Branch 2: valid = false
        CheckTokenResponse invalidCase = new CheckTokenResponse(false);
        assertThat(invalidCase.getMessage()).isEqualTo("Invalid token");

        // Verify they are different
        assertThat(validCase.getMessage()).isNotEqualTo(invalidCase.getMessage());
    }
}

