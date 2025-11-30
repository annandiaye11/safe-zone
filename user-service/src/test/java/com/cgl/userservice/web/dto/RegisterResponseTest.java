package com.cgl.userservice.web.dto;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterResponseTest {

    @Test
    void testNoArgsConstructor() {
        // When
        RegisterResponse response = new RegisterResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isZero();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        RegisterResponse response = new RegisterResponse(
                200,
                "Registration successful",
                "user-123",
                "John Doe",
                "john@example.com"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("Registration successful");
        assertThat(response.getId()).isEqualTo("user-123");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testUserConstructor_WithClientUser() {
        // Given
        User user = User.builder()
                .id("client-123")
                .name("Alice Client")
                .email("alice@example.com")
                .password("encodedPassword")
                .role(Role.CLIENT)
                .build();

        // When
        RegisterResponse response = new RegisterResponse(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("client-123");
        assertThat(response.getName()).isEqualTo("Alice Client");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        // Status and message are not set by this constructor
        assertThat(response.getStatus()).isZero();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testUserConstructor_WithSellerUser() {
        // Given
        User user = User.builder()
                .id("seller-456")
                .name("Bob Seller")
                .email("bob@example.com")
                .password("encodedPassword")
                .role(Role.SELLER)
                .avatar("avatar-url")
                .build();

        // When
        RegisterResponse response = new RegisterResponse(user);

        // Then
        assertThat(response.getId()).isEqualTo("seller-456");
        assertThat(response.getName()).isEqualTo("Bob Seller");
        assertThat(response.getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void testBuilder() {
        // When
        RegisterResponse response = RegisterResponse.builder()
                .status(201)
                .message("User registered successfully")
                .id("builder-123")
                .name("Builder User")
                .email("builder@example.com")
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getMessage()).isEqualTo("User registered successfully");
        assertThat(response.getId()).isEqualTo("builder-123");
        assertThat(response.getName()).isEqualTo("Builder User");
        assertThat(response.getEmail()).isEqualTo("builder@example.com");
    }

    @Test
    void testGettersAndSetters() {
        // Given
        RegisterResponse response = new RegisterResponse();

        // When
        response.setStatus(200);
        response.setMessage("Success");
        response.setId("setter-123");
        response.setName("Setter User");
        response.setEmail("setter@example.com");

        // Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getId()).isEqualTo("setter-123");
        assertThat(response.getName()).isEqualTo("Setter User");
        assertThat(response.getEmail()).isEqualTo("setter@example.com");
    }

    @Test
    void testUserConstructor_OnlyMapsCertainFields() {
        // Given - User with all fields
        User user = User.builder()
                .id("full-user-id")
                .name("Full User")
                .email("full@example.com")
                .password("password-should-not-be-mapped")
                .role(Role.CLIENT)
                .avatar("avatar-url")
                .build();

        // When
        RegisterResponse response = new RegisterResponse(user);

        // Then - Only id, name, email are mapped
        assertThat(response.getId()).isEqualTo("full-user-id");
        assertThat(response.getName()).isEqualTo("Full User");
        assertThat(response.getEmail()).isEqualTo("full@example.com");
        // Password and role should NOT be in the response
    }

    @Test
    void testBuilderWithPartialData() {
        // When - Only some fields set
        RegisterResponse response = RegisterResponse.builder()
                .status(400)
                .message("Validation error")
                .build();

        // Then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("Validation error");
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testSettersChainingNotSupported() {
        // Given
        RegisterResponse response = new RegisterResponse();

        // When - Set multiple values
        response.setStatus(200);
        response.setMessage("OK");
        response.setId("chain-123");

        // Then - All values should be set
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("OK");
        assertThat(response.getId()).isEqualTo("chain-123");
    }

    @Test
    void testTypicalSuccessResponse() {
        // Given
        User registeredUser = User.builder()
                .id("new-user-id")
                .name("New User")
                .email("newuser@example.com")
                .role(Role.CLIENT)
                .build();

        // When - Typical usage scenario
        RegisterResponse response = new RegisterResponse(registeredUser);
        response.setStatus(201);
        response.setMessage("Registration successful");

        // Then
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getMessage()).isEqualTo("Registration successful");
        assertThat(response.getId()).isEqualTo("new-user-id");
        assertThat(response.getName()).isEqualTo("New User");
        assertThat(response.getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    void testTypicalErrorResponse() {
        // When - Error scenario
        RegisterResponse response = RegisterResponse.builder()
                .status(409)
                .message("User already exists")
                .build();

        // Then
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getMessage()).isEqualTo("User already exists");
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testBuilderAllFieldsSet() {
        // When
        RegisterResponse response = RegisterResponse.builder()
                .status(200)
                .message("OK")
                .id("test-id")
                .name("Test Name")
                .email("test@example.com")
                .build();

        // Then - Verify all fields are properly set
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo("OK");
        assertThat(response.getId()).isEqualTo("test-id");
        assertThat(response.getName()).isEqualTo("Test Name");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testUserConstructorWithNullFields() {
        // Given - User with some null fields
        User user = User.builder()
                .id("partial-user")
                .name(null)
                .email("partial@example.com")
                .build();

        // When
        RegisterResponse response = new RegisterResponse(user);

        // Then
        assertThat(response.getId()).isEqualTo("partial-user");
        assertThat(response.getName()).isNull();
        assertThat(response.getEmail()).isEqualTo("partial@example.com");
    }

    @Test
    void testSettersWithNullValues() {
        // Given
        RegisterResponse response = new RegisterResponse();

        // When
        response.setStatus(0);
        response.setMessage(null);
        response.setId(null);
        response.setName(null);
        response.setEmail(null);

        // Then
        assertThat(response.getStatus()).isZero();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testStatusCodes() {
        // When - Test different status codes
        RegisterResponse success = new RegisterResponse();
        success.setStatus(201);

        RegisterResponse conflict = new RegisterResponse();
        conflict.setStatus(409);

        RegisterResponse badRequest = new RegisterResponse();
        badRequest.setStatus(400);

        // Then
        assertThat(success.getStatus()).isEqualTo(201);
        assertThat(conflict.getStatus()).isEqualTo(409);
        assertThat(badRequest.getStatus()).isEqualTo(400);
    }

    @Test
    void testLongMessageAndNames() {
        // When - Test with long strings
        String longMessage = "This is a very long message that explains in detail what happened during the registration process";
        String longName = "Very Long Name With Multiple Words And Special Characters !@#$%";

        RegisterResponse response = RegisterResponse.builder()
                .status(200)
                .message(longMessage)
                .name(longName)
                .email("test@example.com")
                .build();

        // Then
        assertThat(response.getMessage()).isEqualTo(longMessage);
        assertThat(response.getName()).isEqualTo(longName);
    }
}

