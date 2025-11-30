package com.cgl.userservice.services.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.data.repositories.UserRepository;
import com.cgl.userservice.exception.UnauthorizedException;
import com.cgl.userservice.services.UserEventPublisher;
import com.cgl.userservice.web.dto.ChangePasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventPublisher userEventPublisher;

    @Mock
    private S3Service s3Service;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.CLIENT)
                .avatar("avatar-url")
                .build();
    }

    // ==================== getAllUsers() TESTS ====================

    @Test
    void testGetAllUsers_ReturnsUsersList() {
        // Given
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("user-123");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_ReturnsEmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    // ==================== getById() TESTS ====================

    @Test
    void testGetById_UserExists_ReturnsUser() {
        // Given
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getById("user-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-123");
        verify(userRepository, times(1)).findById("user-123");
    }

    @Test
    void testGetById_UserNotFound_ReturnsNull() {
        // Given - Condition: orElse(null)
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        User result = userService.getById("non-existent");

        // Then
        assertThat(result).isNull();
        verify(userRepository, times(1)).findById("non-existent");
    }

    // ==================== getByEmail() TESTS ====================

    @Test
    void testGetByEmail_UserExists_ReturnsUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getByEmail("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetByEmail_UserNotFound_ReturnsNull() {
        // Given - Condition: orElse(null)
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        User result = userService.getByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isNull();
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    // ==================== create() TESTS ====================

    @Test
    void testCreate_EncodesPasswordAndSavesUser() {
        // Given
        User newUser = User.builder()
                .email("new@example.com")
                .password("plainPassword")
                .role(Role.CLIENT)
                .build();

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.create(newUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(newUser);
    }

    // ==================== update() TESTS ====================

    @Test
    void testUpdate_SavesAndReturnsUser() {
        // Given
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.update(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-123");
        verify(userRepository, times(1)).save(testUser);
    }

    // ==================== delete() TESTS ====================

    @Test
    void testDelete_DeletesUserAndPublishesEvent() {
        // Given
        doNothing().when(userRepository).delete(testUser);
        doNothing().when(userEventPublisher).sendDeleteEvent("user-123");

        // When
        User result = userService.delete(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-123");
        verify(userRepository, times(1)).delete(testUser);
        verify(userEventPublisher, times(1)).sendDeleteEvent("user-123");
    }

    // ==================== getCurrentUser() TESTS ====================

    @Test
    void testGetCurrentUser_AuthenticatedUser_ReturnsUser() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getCurrentUser();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetCurrentUser_UserNotFound_ThrowsException() {
        // Given - Condition: orElseThrow
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetCurrentUser_NotAuthenticated_ThrowsUnauthorizedException() {
        // Given - Condition: authentication != null && isAuthenticated()
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");
    }

    @Test
    void testGetCurrentUser_NullAuthentication_ThrowsUnauthorizedException() {
        // Given - Condition: authentication != null
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");
    }

    // ==================== updateAvatar() TESTS ====================

    @Test
    void testUpdateAvatar_UserExists_UpdatesAvatar() {
        // Given
        MultipartFile imageFile = mock(MultipartFile.class);
        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(s3Service.uploadFile(imageFile)).thenReturn("new-avatar-url");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.updateAvatar(imageFile, "user-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAvatar()).isEqualTo("new-avatar-url");
        verify(s3Service, times(1)).uploadFile(imageFile);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateAvatar_UserNotFound_ReturnsNull() {
        // Given - Condition: user == null
        MultipartFile imageFile = mock(MultipartFile.class);
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        User result = userService.updateAvatar(imageFile, "non-existent");

        // Then
        assertThat(result).isNull();
        verify(userRepository, times(1)).findById("non-existent");
        verify(s3Service, never()).uploadFile(any());
        verify(userRepository, never()).save(any());
    }

    // ==================== updatePassword() TESTS ====================

    @Test
    void testUpdatePassword_Success_ReturnsSuccessMessage() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123!");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123!")).thenReturn("newEncodedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        Map<String, Object> result = userService.updatePassword("user-123", request);

        // Then
        assertThat(result).containsEntry("message", "Le mot de passe a été changé").containsKey("user");
        assertThat(testUser.getPassword()).isEqualTo("newEncodedPassword");
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword123!");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdatePassword_UserNotFound_ReturnsErrorMessage() {
        // Given - Condition: user == null
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123!");

        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = userService.updatePassword("non-existent", request);

        // Then
        assertThat(result).containsEntry("message", "Cet utilisateur n'existe pas").doesNotContainKey("user");
        verify(userRepository, times(1)).findById("non-existent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdatePassword_IncorrectCurrentPassword_ReturnsErrorMessage() {
        // Given - Condition: !passwordEncoder.matches()
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123!");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When
        Map<String, Object> result = userService.updatePassword("user-123", request);

        // Then
        assertThat(result).containsEntry("message", "Le mot de passe actuel n'est pas correcte").doesNotContainKey("user");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    // ==================== ADDITIONAL INTEGRATION TESTS ====================

    @Test
    void testConstants() {
        // Then
        assertThat(UserServiceImpl.MESSAGE_KEY).isEqualTo("message");
    }

    @Test
    void testConstructor() {
        // When
        UserServiceImpl service = new UserServiceImpl(userRepository, passwordEncoder, userEventPublisher, s3Service);

        // Then
        assertThat(service).isNotNull();
    }

    @Test
    void testGetCurrentUserEmail_WithPrincipalNotUserDetails_ThrowsException() {
        // Given - Condition: principal instanceof UserDetails
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("notUserDetails"); // String instead of UserDetails

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User is not authenticated");
    }

    @Test
    void testCreate_VerifyPasswordIsEncoded() {
        // Given
        User newUser = User.builder()
                .email("verify@example.com")
                .password("rawPassword123")
                .role(Role.SELLER)
                .build();

        when(passwordEncoder.encode("rawPassword123")).thenReturn("superSecureHash");
        when(userRepository.save(newUser)).thenReturn(newUser);

        // When
        userService.create(newUser);

        // Then - Verify password was set to encoded value
        assertThat(newUser.getPassword()).isEqualTo("superSecureHash");
    }

    @Test
    void testUpdatePassword_VerifyPasswordIsUpdated() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPass");
        request.setNewPassword("newPass123");

        User user = User.builder()
                .id("user-456")
                .email("update@example.com")
                .password("oldHash")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findById("user-456")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHash");
        when(userRepository.save(user)).thenReturn(user);

        // When
        userService.updatePassword("user-456", request);

        // Then
        assertThat(user.getPassword()).isEqualTo("newHash");
    }
}

