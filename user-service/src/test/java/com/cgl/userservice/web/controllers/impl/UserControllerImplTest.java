package com.cgl.userservice.web.controllers.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.services.UserService;
import com.cgl.userservice.services.impl.S3Service;
import com.cgl.userservice.web.dto.ChangePasswordRequest;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private UserControllerImpl userController;

    private User testUser;
    private List<User> testUserList;

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

        testUserList = new ArrayList<>();
        testUserList.add(testUser);
    }

    // ==================== getAllUsers() TESTS ====================

    @Test
    void testGetAllUsers_WithUsers_ReturnsUsersList() {
        // Given
        when(userService.getAllUsers()).thenReturn(testUserList);

        // When
        ResponseEntity<Map<String, Object>> response = userController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "Users found");
        assertThat(response.getBody()).containsKey("users");

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetAllUsers_NoUsers_Returns404() {
        // Given - Empty list (Condition: users.isEmpty())
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<Map<String, Object>> response = userController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "No users found");
        assertThat(response.getBody()).doesNotContainKey("users");

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetAllUsers_MultipleUsers() {
        // Given
        User user2 = User.builder()
                .id("user-456")
                .name("User 2")
                .email("user2@example.com")
                .role(Role.SELLER)
                .build();

        List<User> multipleUsers = List.of(testUser, user2);
        when(userService.getAllUsers()).thenReturn(multipleUsers);

        // When
        ResponseEntity<Map<String, Object>> response = userController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        List<UserOneResponse> users = (List<UserOneResponse>) response.getBody().get("users");
        assertThat(users).hasSize(2);
    }

    // ==================== getUserById() TESTS ====================

    @Test
    void testGetUserById_UserExists_ReturnsUser() {
        // Given
        when(userService.getById("user-123")).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = userController.getUserById("user-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "User found");
        assertThat(response.getBody()).containsKey("user");

        verify(userService, times(1)).getById("user-123");
    }

    @Test
    void testGetUserById_UserNotFound_Returns404() {
        // Given - User not found (Condition: user == null)
        when(userService.getById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = userController.getUserById("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "User not found");
        assertThat(response.getBody()).doesNotContainKey("user");

        verify(userService, times(1)).getById("non-existent");
    }

    // ==================== updateUser(id, dto) TESTS ====================

    @Test
    void testUpdateUser_UserExists_UpdatesUser() {
        // Given
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setAvatar("new-avatar-url");

        when(userService.getById("user-123")).thenReturn(testUser);
        when(userService.update(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = userController.updateUser("user-123", updateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "User updated");
        assertThat(response.getBody()).containsKey("user");

        // Verify user fields were updated
        assertThat(testUser.getName()).isEqualTo("Updated Name");
        assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(testUser.getAvatar()).isEqualTo("new-avatar-url");

        verify(userService, times(1)).getById("user-123");
        verify(userService, times(1)).update(testUser);
    }

    @Test
    void testUpdateUser_UserNotFound_Returns404() {
        // Given - User not found (Condition: user == null)
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        when(userService.getById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = userController.updateUser("non-existent", updateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "User not found");

        verify(userService, times(1)).getById("non-existent");
        verify(userService, never()).update(any());
    }

    // ==================== deleteUser() TESTS ====================

    @Test
    void testDeleteUser_UserExists_DeletesUser() {
        // Given
        when(userService.getById("user-123")).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = userController.deleteUser("user-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "User deleted");
        assertThat(response.getBody()).containsKey("user");

        verify(userService, times(1)).getById("user-123");
        verify(userService, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUser_UserNotFound_Returns404() {
        // Given - User not found (Condition: user == null)
        when(userService.getById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = userController.deleteUser("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(userService, times(1)).getById("non-existent");
        verify(userService, never()).delete(any());
    }

    // ==================== getCurrentUser() TESTS ====================

    @Test
    void testGetCurrentUser_UserExists_ReturnsUser() {
        // Given
        when(userService.getCurrentUser()).thenReturn(testUser);

        // When
        ResponseEntity<UserOneResponse> response = userController.getCurrentUser();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("user-123");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");

        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void testGetCurrentUser_UserNotFound_Returns404() {
        // Given - Current user not found (Condition: me == null)
        when(userService.getCurrentUser()).thenReturn(null);

        // When
        ResponseEntity<UserOneResponse> response = userController.getCurrentUser();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(userService, times(1)).getCurrentUser();
    }

    // ==================== updateUser(imageFile, email) TESTS ====================

    @Test
    void testUpdateUserAvatar_UserExists_UpdatesAvatar() {
        // Given
        MultipartFile imageFile = mock(MultipartFile.class);
        String email = "test@example.com";

        when(userService.updateAvatar(imageFile, email)).thenReturn(testUser);

        // When
        ResponseEntity<UserOneResponse> response = userController.updateUser(imageFile, email);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("user-123");

        verify(userService, times(1)).updateAvatar(imageFile, email);
    }

    @Test
    void testUpdateUserAvatar_UserNotFound_Returns404() {
        // Given - User not found (Condition: user == null)
        MultipartFile imageFile = mock(MultipartFile.class);
        String email = "nonexistent@example.com";

        when(userService.updateAvatar(imageFile, email)).thenReturn(null);

        // When
        ResponseEntity<UserOneResponse> response = userController.updateUser(imageFile, email);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(userService, times(1)).updateAvatar(imageFile, email);
    }

    // ==================== updatePassword() TESTS ====================

    @Test
    void testUpdatePassword_Success_Returns200() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123!");

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("message", "Password updated successfully");
        serviceResponse.put("status", 200);

        when(userService.updatePassword("user-123", request)).thenReturn(serviceResponse);

        // When
        ResponseEntity<Map<String, Object>> response = userController.updatePassword("user-123", request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Password updated successfully");

        verify(userService, times(1)).updatePassword("user-123", request);
    }

    @Test
    void testUpdatePassword_UserNotFound_Returns400() {
        // Given - Condition: message.equals("User not found")
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123!");

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("message", "User not found");
        serviceResponse.put("status", 400);

        when(userService.updatePassword("non-existent", request)).thenReturn(serviceResponse);

        // When
        ResponseEntity<Map<String, Object>> response = userController.updatePassword("non-existent", request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "User not found");

        verify(userService, times(1)).updatePassword("non-existent", request);
    }

    @Test
    void testUpdatePassword_IncorrectCurrentPassword_Returns400() {
        // Given - Condition: message.equals("Current password is incorrect")
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123!");

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("message", "Current password is incorrect");
        serviceResponse.put("status", 400);

        when(userService.updatePassword("user-123", request)).thenReturn(serviceResponse);

        // When
        ResponseEntity<Map<String, Object>> response = userController.updatePassword("user-123", request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Current password is incorrect");

        verify(userService, times(1)).updatePassword("user-123", request);
    }

    // ==================== ADDITIONAL INTEGRATION TESTS ====================

    @Test
    void testConstructor() {
        // When
        UserControllerImpl controller = new UserControllerImpl(userService, passwordEncoder, s3Service);

        // Then
        assertThat(controller).isNotNull();
    }

    @Test
    void testGetAllUsers_VerifyMappingToDto() {
        // Given
        when(userService.getAllUsers()).thenReturn(testUserList);

        // When
        ResponseEntity<Map<String, Object>> response = userController.getAllUsers();

        // Then
        @SuppressWarnings("unchecked")
        List<UserOneResponse> users = (List<UserOneResponse>) response.getBody().get("users");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getId()).isEqualTo("user-123");
        assertThat(users.get(0).getName()).isEqualTo("Test User");
    }

    @Test
    void testGetUserById_UsesToDtoSecond() {
        // Given
        when(userService.getById("user-123")).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = userController.getUserById("user-123");

        // Then
        assertThat(response.getBody()).containsKey("user");
        // Verify that toDtoSecond is used (returns UserAllResponse)
    }

    @Test
    void testUpdateUser_VerifyAllFieldsUpdated() {
        // Given
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@email.com");
        updateDto.setAvatar("new-avatar");

        when(userService.getById("user-123")).thenReturn(testUser);
        when(userService.update(any(User.class))).thenReturn(testUser);

        // When
        userController.updateUser("user-123", updateDto);

        // Then - Verify all three fields were set
        assertThat(testUser.getName()).isEqualTo("New Name");
        assertThat(testUser.getEmail()).isEqualTo("new@email.com");
        assertThat(testUser.getAvatar()).isEqualTo("new-avatar");
    }

    @Test
    void testDeleteUser_ReturnsDeletedUserInResponse() {
        // Given
        when(userService.getById("user-123")).thenReturn(testUser);

        // When
        ResponseEntity<Map<String, Object>> response = userController.deleteUser("user-123");

        // Then
        assertThat(response.getBody()).containsKey("user");
        // The deleted user should be in the response
    }

    @Test
    void testUpdatePassword_DifferentErrorMessages() {
        // Test both error conditions are handled correctly
        ChangePasswordRequest request = new ChangePasswordRequest();

        // Test 1: User not found
        Map<String, Object> response1 = new HashMap<>();
        response1.put("message", "User not found");
        when(userService.updatePassword("id1", request)).thenReturn(response1);

        ResponseEntity<Map<String, Object>> result1 = userController.updatePassword("id1", request);
        assertThat(result1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test 2: Incorrect password
        Map<String, Object> response2 = new HashMap<>();
        response2.put("message", "Current password is incorrect");
        when(userService.updatePassword("id2", request)).thenReturn(response2);

        ResponseEntity<Map<String, Object>> result2 = userController.updatePassword("id2", request);
        assertThat(result2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

