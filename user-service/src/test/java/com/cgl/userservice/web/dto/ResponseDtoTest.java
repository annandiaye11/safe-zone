package com.cgl.userservice.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        // When
        ResponseDto response = new ResponseDto();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        ResponseDto response = new ResponseDto("jwt-token-123", "user@example.com");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void testTokenOnlyConstructor() {
        // When - Test custom constructor with only token
        ResponseDto response = new ResponseDto("custom-jwt-token");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("custom-jwt-token");
        assertThat(response.getEmail()).isNull(); // Not set by this constructor
    }

    @Test
    void testTokenOnlyConstructor_WithDifferentToken() {
        // When
        ResponseDto response = new ResponseDto("another-token-456");

        // Then
        assertThat(response.getToken()).isEqualTo("another-token-456");
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testTokenOnlyConstructor_WithNullToken() {
        // When
        ResponseDto response = new ResponseDto((String) null);

        // Then
        assertThat(response.getToken()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testTokenOnlyConstructor_WithEmptyToken() {
        // When
        ResponseDto response = new ResponseDto("");

        // Then
        assertThat(response.getToken()).isEmpty();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testBuilder() {
        // When
        ResponseDto response = ResponseDto.builder()
                .token("builder-token-789")
                .email("builder@example.com")
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("builder-token-789");
        assertThat(response.getEmail()).isEqualTo("builder@example.com");
    }

    @Test
    void testBuilderWithPartialData() {
        // When - Only token set
        ResponseDto response = ResponseDto.builder()
                .token("partial-token")
                .build();

        // Then
        assertThat(response.getToken()).isEqualTo("partial-token");
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testGettersAndSetters() {
        // Given
        ResponseDto response = new ResponseDto();

        // When
        response.setToken("setter-token");
        response.setEmail("setter@example.com");

        // Then
        assertThat(response.getToken()).isEqualTo("setter-token");
        assertThat(response.getEmail()).isEqualTo("setter@example.com");
    }

    @Test
    void testSetToken() {
        // Given
        ResponseDto response = new ResponseDto();

        // When
        response.setToken("new-token");

        // Then
        assertThat(response.getToken()).isEqualTo("new-token");
    }

    @Test
    void testSetEmail() {
        // Given
        ResponseDto response = new ResponseDto();

        // When
        response.setEmail("test@example.com");

        // Then
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testTypicalUsageScenario_Login() {
        // Given - Typical login response scenario
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dozjgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U";
        String userEmail = "john.doe@example.com";

        // When - Create response using custom constructor
        ResponseDto response = new ResponseDto(jwtToken);
        response.setEmail(userEmail);

        // Then
        assertThat(response.getToken()).isEqualTo(jwtToken);
        assertThat(response.getEmail()).isEqualTo(userEmail);
    }

    @Test
    void testTypicalUsageScenario_PasswordChange() {
        // When - Password change response (only token, no email needed)
        ResponseDto response = new ResponseDto("password-change-token-abc");

        // Then
        assertThat(response.getToken()).isNotNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testBuilderWithBothFields() {
        // When
        ResponseDto response = ResponseDto.builder()
                .token("complete-token-xyz")
                .email("complete@example.com")
                .build();

        // Then
        assertThat(response.getToken()).isEqualTo("complete-token-xyz");
        assertThat(response.getEmail()).isEqualTo("complete@example.com");
    }

    @Test
    void testSettersWithNullValues() {
        // Given
        ResponseDto response = new ResponseDto("initial-token", "initial@example.com");

        // When
        response.setToken(null);
        response.setEmail(null);

        // Then
        assertThat(response.getToken()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void testLongJwtToken() {
        // Given - Real-world JWT token (long string)
        String longToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjE2MTYzMjU0MjJ9.veryLongSignaturePartThatRepresentsARealJWTTokenWithAllTheNecessaryInformationAndSecurityFeatures";

        // When
        ResponseDto response = new ResponseDto(longToken);

        // Then
        assertThat(response.getToken()).isEqualTo(longToken);
        assertThat(response.getToken().length()).isGreaterThan(100);
    }

    @Test
    void testUpdateTokenAfterCreation() {
        // Given
        ResponseDto response = new ResponseDto("old-token");

        // When
        response.setToken("new-refreshed-token");

        // Then
        assertThat(response.getToken()).isEqualTo("new-refreshed-token");
    }

    @Test
    void testUpdateEmailAfterCreation() {
        // Given
        ResponseDto response = new ResponseDto("token");

        // When - Add email after creation
        response.setEmail("updated@example.com");

        // Then
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testMultipleSettersSequentially() {
        // Given
        ResponseDto response = new ResponseDto();

        // When
        response.setToken("token1");
        response.setEmail("email1@example.com");
        response.setToken("token2");
        response.setEmail("email2@example.com");

        // Then - Last values should persist
        assertThat(response.getToken()).isEqualTo("token2");
        assertThat(response.getEmail()).isEqualTo("email2@example.com");
    }

    @Test
    void testConstructorDisambiguation() {
        // When - Ensure the single-argument constructor is called correctly
        String tokenValue = "disambiguation-test-token";
        ResponseDto response = new ResponseDto(tokenValue);

        // Then
        assertThat(response.getToken()).isEqualTo(tokenValue);
        assertThat(response.getEmail()).isNull();
    }
}

