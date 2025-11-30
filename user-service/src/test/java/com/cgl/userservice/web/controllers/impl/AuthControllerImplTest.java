package com.cgl.userservice.web.controllers.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.services.UserService;
import com.cgl.userservice.utils.JwtTools;
import com.cgl.userservice.web.dto.RequestDto;
import com.cgl.userservice.web.dto.ResponseDto;
import com.cgl.userservice.web.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTools jwtTools;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthControllerImpl authController;

    private User testUser;
    private RequestDto loginRequest;
    private UserDto registerRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .password("$2a$10$encodedPassword")
                .name("Test User")
                .role(Role.CLIENT)
                .build();

        // Setup login request
        loginRequest = new RequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("plainPassword");

        // Setup register request
        registerRequest = new UserDto();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newPassword123");
        registerRequest.setName("New User");
        registerRequest.setRole("CLIENT");
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void testLogin_Success() {
        // Given
        String expectedToken = "jwt.token.here";
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("plainPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtTools.generateToken(testUser)).thenReturn(expectedToken);

        // When
        ResponseEntity<ResponseDto> response = authController.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get(HttpHeaders.CACHE_CONTROL)).contains("no-store");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(expectedToken);

        verify(userService, times(1)).getByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("plainPassword", "$2a$10$encodedPassword");
        verify(jwtTools, times(1)).generateToken(testUser);
    }

    @Test
    void testLogin_UserNotFound_ThrowsUnauthorized() {
        // Given
        when(userService.getByEmail("test@example.com")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Incorrect email")
                .satisfies(exception -> {
                    ResponseStatusException rse = (ResponseStatusException) exception;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });

        verify(userService, times(1)).getByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTools, never()).generateToken(any());
    }

    @Test
    void testLogin_IncorrectPassword_ThrowsUnauthorized() {
        // Given
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("plainPassword", "$2a$10$encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Incorrect password")
                .satisfies(exception -> {
                    ResponseStatusException rse = (ResponseStatusException) exception;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });

        verify(userService, times(1)).getByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("plainPassword", "$2a$10$encodedPassword");
        verify(jwtTools, never()).generateToken(any());
    }

    @Test
    void testLogin_WithDifferentEmail() {
        // Given
        loginRequest.setEmail("different@example.com");
        when(userService.getByEmail("different@example.com")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Incorrect email");

        verify(userService, times(1)).getByEmail("different@example.com");
    }

    @Test
    void testLogin_WithEmptyPassword() {
        // Given
        loginRequest.setPassword("");
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("", "$2a$10$encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Incorrect password");
    }

    // ==================== REGISTER TESTS ====================

    @Test
    void testRegister_Success() {
        // Given
        User createdUser = User.builder()
                .id("new-user-123")
                .email("newuser@example.com")
                .name("New User")
                .role(Role.CLIENT)
                .build();

        when(userService.getByEmail("newuser@example.com")).thenReturn(null);
        when(userService.create(any(User.class))).thenReturn(createdUser);

        // When
        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().get(HttpHeaders.CACHE_CONTROL)).contains("no-store");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "User created");
        assertThat(response.getBody().get("response")).isNotNull();

        verify(userService, times(1)).getByEmail("newuser@example.com");
        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Given
        when(userService.getByEmail("newuser@example.com")).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "User already exists");
        assertThat(response.getBody().get("response")).isNull();

        verify(userService, times(1)).getByEmail("newuser@example.com");
        verify(userService, never()).create(any(User.class));
    }

    @Test
    void testRegister_WithSellerRole() {
        // Given
        registerRequest.setRole("SELLER");
        User sellerUser = User.builder()
                .id("seller-123")
                .email("seller@example.com")
                .name("Seller User")
                .role(Role.SELLER)
                .build();

        when(userService.getByEmail("newuser@example.com")).thenReturn(null);
        when(userService.create(any(User.class))).thenReturn(sellerUser);

        // When
        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "User created");

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void testRegister_WithExistingEmailDifferentCase() {
        // Given
        registerRequest.setEmail("TEST@EXAMPLE.COM");
        when(userService.getByEmail("TEST@EXAMPLE.COM")).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "User already exists");

        verify(userService, times(1)).getByEmail("TEST@EXAMPLE.COM");
        verify(userService, never()).create(any(User.class));
    }

    @Test
    void testRegister_VerifyResponseStructure() {
        // Given
        User createdUser = User.builder()
                .id("user-456")
                .email("verify@example.com")
                .name("Verify User")
                .role(Role.CLIENT)
                .build();

        registerRequest.setEmail("verify@example.com");
        when(userService.getByEmail("verify@example.com")).thenReturn(null);
        when(userService.create(any(User.class))).thenReturn(createdUser);

        // When
        ResponseEntity<Map<String, Object>> response = authController.register(registerRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKeys("message", "response");
        assertThat(response.getBody()).containsEntry("message", "User created");
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testRegister_MultipleCalls() {
        // Given
        User createdUser1 = User.builder().id("user-1").email("user1@example.com").build();
        User createdUser2 = User.builder().id("user-2").email("user2@example.com").build();

        when(userService.getByEmail("user1@example.com")).thenReturn(null);
        when(userService.getByEmail("user2@example.com")).thenReturn(null);
        when(userService.create(any(User.class))).thenReturn(createdUser1, createdUser2);

        // When - First call
        registerRequest.setEmail("user1@example.com");
        ResponseEntity<Map<String, Object>> response1 = authController.register(registerRequest);

        // When - Second call
        registerRequest.setEmail("user2@example.com");
        ResponseEntity<Map<String, Object>> response2 = authController.register(registerRequest);

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(userService, times(2)).create(any(User.class));
    }
}

