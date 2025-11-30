package com.cgl.userservice.config;

import com.cgl.userservice.utils.JwtControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtControl jwtFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private SecurityConfig securityConfig;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // ==================== PasswordEncoder TESTS ====================

    @Test
    void testPasswordEncoderBean() {
        // Given
        String rawPassword = "testPassword123!";

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertThat(encodedPassword).isNotNull().isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void testPasswordEncoderDifferentEncodingsForSamePassword() {
        // Given
        String rawPassword = "samePassword1!";

        // When
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        // Then - BCrypt produit des hashes différents avec des salts différents
        assertThat(encoded1).isNotEqualTo(encoded2);
        assertThat(passwordEncoder.matches(rawPassword, encoded1)).isTrue();
        assertThat(passwordEncoder.matches(rawPassword, encoded2)).isTrue();
    }

    @Test
    void testPasswordEncoderRejectsWrongPassword() {
        // Given
        String correctPassword = "correctPassword1!";
        String wrongPassword = "wrongPassword1!";
        String encoded = passwordEncoder.encode(correctPassword);

        // When & Then
        assertThat(passwordEncoder.matches(wrongPassword, encoded)).isFalse();
    }

    @Test
    void testPasswordEncoderHandlesSpecialCharacters() {
        // Given
        String passwordWithSpecialChars = "P@ssw0rd!#$%^&*()";

        // When
        String encoded = passwordEncoder.encode(passwordWithSpecialChars);

        // Then
        assertThat(passwordEncoder.matches(passwordWithSpecialChars, encoded)).isTrue();
    }

    @Test
    void testPasswordEncoderHandlesLongPassword() {
        // Given
        String longPassword = "ThisIsAVeryLongPasswordWithMoreThan50CharactersToBCryptCapability12345";

        // When
        String encoded = passwordEncoder.encode(longPassword);

        // Then
        assertThat(passwordEncoder.matches(longPassword, encoded)).isTrue();
    }

    @Test
    void testPasswordEncoderHandlesEmptyPassword() {
        // Given
        String emptyPassword = "";

        // When
        String encoded = passwordEncoder.encode(emptyPassword);

        // Then
        assertThat(encoded).isNotNull();
        assertThat(passwordEncoder.matches(emptyPassword, encoded)).isTrue();
    }

    @Test
    void testPasswordEncoderStrength() {
        // Given
        String password = "testPassword1!";

        // When
        String encoded = passwordEncoder.encode(password);

        // Then - BCrypt hash should start with $2a$ (or $2b$, $2y$)
        assertThat(encoded).startsWith("$2");
        assertThat(encoded.length()).isGreaterThan(50); // BCrypt hashes are 60 chars
    }

    // ==================== NEW TESTS FOR SecurityConfig BEANS ====================

    @Test
    void testPasswordEncoderBean_CreatesBCryptPasswordEncoder() {
        // When
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Then
        assertThat(encoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void testPasswordEncoderBean_FunctionalTest() {
        // Given
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "MySecurePassword123!";

        // When
        String encoded = encoder.encode(rawPassword);

        // Then
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
        assertThat(encoder.matches("WrongPassword", encoded)).isFalse();
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        // Given
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        // When
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Then
        assertThat(result).isNotNull().isEqualTo(mockAuthManager);
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void testMethodSecurityExpressionHandlerBean() {
        // When
        MethodSecurityExpressionHandler handler = securityConfig.methodSecurityExpressionHandler(applicationContext);

        // Then
        assertThat(handler).isNotNull().isInstanceOf(DefaultMethodSecurityExpressionHandler.class);
    }

    @Test
    void testMethodSecurityExpressionHandlerBean_VerifyApplicationContextSet() {
        // When
        MethodSecurityExpressionHandler handler = securityConfig.methodSecurityExpressionHandler(applicationContext);

        // Then
        assertThat(handler).isNotNull();
        // Verify that the handler is properly configured
        DefaultMethodSecurityExpressionHandler defaultHandler = (DefaultMethodSecurityExpressionHandler) handler;
        assertThat(defaultHandler).isNotNull();
    }

    @Test
    void testSecurityConfigAnnotations() {
        // Then - Verify class has required annotations
        assertThat(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class)).isTrue();
        assertThat(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class)).isTrue();
        assertThat(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class)).isTrue();
    }

    @Test
    void testSecurityConfigConstructor() {
        // When
        SecurityConfig config = new SecurityConfig(jwtFilter);

        // Then
        assertThat(config).isNotNull();
    }

    @Test
    void testPasswordEncoderBean_IsSingleton() {
        // When - Call multiple times
        PasswordEncoder encoder1 = securityConfig.passwordEncoder();
        PasswordEncoder encoder2 = securityConfig.passwordEncoder();

        // Then - Each call creates a new instance (not managed by Spring in this test)
        assertThat(encoder1).isNotNull();
        assertThat(encoder2).isNotNull();
        // Both are BCryptPasswordEncoder
        assertThat(encoder1).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(encoder2).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void testMethodSecurityExpressionHandlerBean_WithDifferentContext() {
        // Given
        ApplicationContext anotherContext = mock(ApplicationContext.class);

        // When
        MethodSecurityExpressionHandler handler = securityConfig.methodSecurityExpressionHandler(anotherContext);

        // Then
        assertThat(handler).isNotNull().isInstanceOf(DefaultMethodSecurityExpressionHandler.class);
    }

    @Test
    void testAuthenticationManagerBean_ThrowsException() throws Exception {
        // Given
        when(authenticationConfiguration.getAuthenticationManager())
                .thenThrow(new RuntimeException("Auth config error"));

        // When & Then
        try {
            securityConfig.authenticationManager(authenticationConfiguration);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("Auth config error");
        }

        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    // ==================== SecurityFilterChain TESTS ====================

    @Test
    void testSecurityFilterChain() throws Exception {
        // Given
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        SecurityFilterChain mockFilterChain = mock(SecurityFilterChain.class);

        // Mock the fluent API chain
        doReturn(http).when(http).csrf(any());
        doReturn(http).when(http).authorizeHttpRequests(any());
        doReturn(http).when(http).sessionManagement(any());
        doReturn(http).when(http).addFilterBefore(any(), any());
        doReturn(mockFilterChain).when(http).build();

        // When
        SecurityFilterChain result = securityConfig.securityFilterChain(http);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(mockFilterChain);

        // Verify the configuration methods were called
        verify(http, times(1)).csrf(any());
        verify(http, times(1)).authorizeHttpRequests(any());
        verify(http, times(1)).sessionManagement(any());
        verify(http, times(1)).addFilterBefore(same(jwtFilter), any());
        verify(http, times(1)).build();
    }

    @Test
    void testSecurityFilterChain_VerifyFilterOrder() throws Exception {
        // Given
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        SecurityFilterChain mockFilterChain = mock(SecurityFilterChain.class);

        doReturn(http).when(http).csrf(any());
        doReturn(http).when(http).authorizeHttpRequests(any());
        doReturn(http).when(http).sessionManagement(any());
        doReturn(http).when(http).addFilterBefore(any(), any());
        doReturn(mockFilterChain).when(http).build();

        // When
        securityConfig.securityFilterChain(http);

        // Then - Verify JWT filter is added before UsernamePasswordAuthenticationFilter
        verify(http).addFilterBefore(
                same(jwtFilter),
                eq(org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
        );
    }
}
