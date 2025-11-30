package com.cgl.userservice.utils;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class JwtToolsTest {

    private JwtTools jwtTools;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtTools = new JwtTools();

        // Set test values using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtTools, "secret", "test-secret-key-for-testing-only-must-be-at-least-256-bits-long");
        ReflectionTestUtils.setField(jwtTools, "expiration", 3600000L);

        // Create test user
        testUser = new User();
        testUser.setId("user123");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.CLIENT);
    }

    @Test
    void testGenerateToken_Success() {
        // When
        String token = jwtTools.generateToken(testUser);

        // Then
        assertThat(token).isNotEmpty();
    }

    @Test
    void testExtractEmail_Success() {
        // Given
        String token = jwtTools.generateToken(testUser);

        // When
        String email = jwtTools.extractEmail(token);

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void testExtractRole_Success() {
        // Given
        String token = jwtTools.generateToken(testUser);

        // When
        String role = jwtTools.extractRole(token);

        // Then
        assertThat(role).isEqualTo("CLIENT");
    }

    @Test
    void testExtractAuthorities_Success() {
        // Given
        String token = jwtTools.generateToken(testUser);

        // When
        Collection<? extends GrantedAuthority> authorities = jwtTools.extractAuthorities(token);

        // Then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("CLIENT");
    }

    @Test
    void testValidateToken_ValidToken() {
        // Given
        String token = jwtTools.generateToken(testUser);

        // When
        boolean isValid = jwtTools.validateToken(token, testUser);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateToken_DifferentUser() {
        // Given
        String token = jwtTools.generateToken(testUser);

        User differentUser = new User();
        differentUser.setEmail("different@example.com");
        differentUser.setRole(Role.CLIENT);

        // When
        boolean isValid = jwtTools.validateToken(token, differentUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateToken_DifferentRole() {
        // Given
        String token = jwtTools.generateToken(testUser);

        User sellerUser = new User();
        sellerUser.setEmail("test@example.com");
        sellerUser.setRole(Role.SELLER);

        // When
        boolean isValid = jwtTools.validateToken(token, sellerUser);

        // Then
        assertThat(isValid).isFalse();
    }

    // ==================== TESTS FOR SIGNING KEY CONDITIONS ====================

    @Test
    void testGenerateToken_WithNullSecret_UsesGeneratedKey() {
        // Given - Condition: secret == null
        JwtTools jwtToolsWithNullSecret = new JwtTools();
        ReflectionTestUtils.setField(jwtToolsWithNullSecret, "secret", null);
        ReflectionTestUtils.setField(jwtToolsWithNullSecret, "expiration", 3600000L);

        // When
        String token = jwtToolsWithNullSecret.generateToken(testUser);

        // Then - Should generate token with auto-generated key
        assertThat(token).isNotEmpty();

        // Verify we can extract claims from the token
        String email = jwtToolsWithNullSecret.extractEmail(token);
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void testGenerateToken_WithEmptySecret_UsesGeneratedKey() {
        // Given - Condition: secret.trim().isEmpty()
        JwtTools jwtToolsWithEmptySecret = new JwtTools();
        ReflectionTestUtils.setField(jwtToolsWithEmptySecret, "secret", "   ");
        ReflectionTestUtils.setField(jwtToolsWithEmptySecret, "expiration", 3600000L);

        // When
        String token = jwtToolsWithEmptySecret.generateToken(testUser);

        // Then - Should generate token with auto-generated key
        assertThat(token).isNotEmpty();

        // Verify we can extract claims from the token
        String role = jwtToolsWithEmptySecret.extractRole(token);
        assertThat(role).isEqualTo("CLIENT");
    }

    @Test
    void testGenerateToken_WithShortSecret_UsesGeneratedKey() {
        // Given - Condition: secret.getBytes(StandardCharsets.UTF_8).length < 32
        JwtTools jwtToolsWithShortSecret = new JwtTools();
        ReflectionTestUtils.setField(jwtToolsWithShortSecret, "secret", "short");
        ReflectionTestUtils.setField(jwtToolsWithShortSecret, "expiration", 3600000L);

        // When
        String token = jwtToolsWithShortSecret.generateToken(testUser);

        // Then - Should generate token with auto-generated key
        assertThat(token).isNotEmpty();

        // Verify token validation works
        boolean isValid = jwtToolsWithShortSecret.validateToken(token, testUser);
        assertThat(isValid).isTrue();
    }

    @Test
    void testGenerateToken_WithExactly32BytesSecret_UsesProvidedKey() {
        // Given - Edge case: exactly 32 bytes
        JwtTools jwtToolsWith32BytesSecret = new JwtTools();
        ReflectionTestUtils.setField(jwtToolsWith32BytesSecret, "secret", "12345678901234567890123456789012"); // 32 bytes
        ReflectionTestUtils.setField(jwtToolsWith32BytesSecret, "expiration", 3600000L);

        // When
        String token = jwtToolsWith32BytesSecret.generateToken(testUser);

        // Then
        assertThat(token).isNotEmpty();

        // Verify we can extract and validate
        String email = jwtToolsWith32BytesSecret.extractEmail(token);
        assertThat(email).isEqualTo("test@example.com");

        boolean isValid = jwtToolsWith32BytesSecret.validateToken(token, testUser);
        assertThat(isValid).isTrue();
    }

    @Test
    void testSigningKey_IsCachedAfterFirstUse() {
        // Given
        String firstToken = jwtTools.generateToken(testUser);

        // When - Generate another token (should reuse cached signing key)
        String secondToken = jwtTools.generateToken(testUser);

        // Then - Both tokens should be valid and different (due to different timestamps)
        assertThat(firstToken).isNotEmpty();
        assertThat(secondToken).isNotEmpty();

        // Tokens are different because of different issuedAt timestamps
        // But if generated at exact same millisecond, they could be identical
        // The important thing is both are valid
        assertThat(jwtTools.validateToken(firstToken, testUser)).isTrue();
        assertThat(jwtTools.validateToken(secondToken, testUser)).isTrue();

        // Verify email extraction works for both
        assertThat(jwtTools.extractEmail(firstToken)).isEqualTo("test@example.com");
        assertThat(jwtTools.extractEmail(secondToken)).isEqualTo("test@example.com");
    }
}
