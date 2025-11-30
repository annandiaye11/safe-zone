package com.cgl.userservice.services.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.data.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginServiceImpl loginService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .email("test@example.com")
                .password("$2a$10$encodedPassword")
                .name("Test User")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    void testLoadUserByUsername_UserFound_ReturnsUserDetails() {
        // Given - User exists in database (Line 1: findByEmail returns Optional.of(user))
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = loginService.loadUserByUsername(email);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$encodedPassword");
        assertThat(userDetails.getAuthorities()).isNotEmpty();

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_UserNotFound_ThrowsException() {
        // Given - User not found (Line 2: orElseThrow - throws UsernameNotFoundException)
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loginService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_DifferentEmail() {
        // Given
        String email = "different@example.com";
        User differentUser = User.builder()
                .id("user-456")
                .email(email)
                .password("encoded")
                .name("Different User")
                .role(Role.SELLER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(differentUser));

        // When
        UserDetails userDetails = loginService.loadUserByUsername(email);

        // Then
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails).isInstanceOf(User.class);
    }

    @Test
    void testLoadUserByUsername_SellerRole() {
        // Given - Test with SELLER role
        User seller = User.builder()
                .id("seller-1")
                .email("seller@example.com")
                .password("encoded")
                .name("Seller User")
                .role(Role.SELLER)
                .build();

        when(userRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));

        // When
        UserDetails userDetails = loginService.loadUserByUsername("seller@example.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("seller@example.com");
        assertThat(userDetails.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .contains("ROLE_SELLER");
    }

    @Test
    void testLoadUserByUsername_ClientRole() {
        // Given - Test with CLIENT role
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = loginService.loadUserByUsername("test@example.com");

        // Then
        assertThat(userDetails.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .contains("ROLE_CLIENT");
    }

    @Test
    void testLoadUserByUsername_ReturnsUserEntity() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = loginService.loadUserByUsername("test@example.com");

        // Then - Verify it returns the actual User entity (which implements UserDetails)
        assertThat(userDetails).isInstanceOf(User.class);
        User user = (User) userDetails;
        assertThat(user.getId()).isEqualTo("user-123");
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void testLoadUserByUsername_EmailCaseSensitivity() {
        // Given - Test different email cases
        String upperCaseEmail = "TEST@EXAMPLE.COM";
        when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loginService.loadUserByUsername(upperCaseEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testLoadUserByUsername_EmptyEmail() {
        // Given
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loginService.loadUserByUsername(emptyEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testLoadUserByUsername_NullEmail() {
        // Given
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loginService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testLoadUserByUsername_MultipleCalls() {
        // Given - Test multiple calls with same email
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When - Call multiple times
        UserDetails userDetails1 = loginService.loadUserByUsername(email);
        UserDetails userDetails2 = loginService.loadUserByUsername(email);

        // Then
        assertThat(userDetails1).isNotNull();
        assertThat(userDetails2).isNotNull();
        verify(userRepository, times(2)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_VerifyRepositoryInteraction() {
        // Given
        String email = "verify@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        loginService.loadUserByUsername(email);

        // Then - Verify repository method was called with correct parameter
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).findByEmail(argThat(arg -> !email.equals(arg)));
    }

    @Test
    void testLoadUserByUsername_UserDetailsContract() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = loginService.loadUserByUsername("test@example.com");

        // Then - Verify UserDetails contract
        assertThat(userDetails.getUsername()).isNotNull();
        assertThat(userDetails.getPassword()).isNotNull();
        assertThat(userDetails.getAuthorities()).isNotNull();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void testLoadUserByUsername_WithComplexPassword() {
        // Given - User with complex password
        User userWithComplexPassword = User.builder()
                .id("user-789")
                .email("complex@example.com")
                .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
                .name("Complex User")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findByEmail("complex@example.com"))
                .thenReturn(Optional.of(userWithComplexPassword));

        // When
        UserDetails userDetails = loginService.loadUserByUsername("complex@example.com");

        // Then
        assertThat(userDetails.getPassword()).startsWith("$2a$");
        assertThat(userDetails.getPassword()).hasSize(60); // BCrypt hash length
    }
}

