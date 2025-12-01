package com.cgl.userservice.web.controllers.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.services.UserService;
import com.cgl.userservice.utils.mapper.MapperUser;
import com.cgl.userservice.web.controllers.UserController;
import com.cgl.userservice.web.dto.ChangePasswordRequest;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;
import com.example.common.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    static final String USER_NOT_FOUND_MESSAGE = "User not found";
    static final String MESSAGE_KEY = "message";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Override
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        if (users.isEmpty()) {
            response.put(MESSAGE_KEY, "No users found");
            return ResponseEntity.status(404).body(response);
        }
        List<UserOneResponse> userResponses = users.stream()
                .map(MapperUser::toDto)
                .toList();
        response.put(MESSAGE_KEY, "Users found");
        response.put("users", userResponses);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUserById(String id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getById(id);
        if (user == null) {
            response.put(MESSAGE_KEY, USER_NOT_FOUND_MESSAGE);
            return ResponseEntity.status(404).body(response);
        }
        response.put(MESSAGE_KEY, "User found");
        response.put("user", MapperUser.toDtoSecond(user));
        return ResponseEntity.status(200).body(response);
    }


    @Override
    public ResponseEntity<Map<String, Object>> updateUser(String id, UserDto userDto) {
        User user = userService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put(MESSAGE_KEY, USER_NOT_FOUND_MESSAGE);
            return ResponseEntity.status(404).body(response);
        }
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setAvatar(userDto.getAvatar());
        userService.update(user);
        response.put(MESSAGE_KEY, "User updated");
        response.put("user", MapperUser.toDto(user));
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteUser(String id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(user);
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "User deleted");
        response.put("user", MapperUser.toDto(user));
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<UserOneResponse> getCurrentUser() {

        User me = userService.getCurrentUser();

        if (me == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(200).body(MapperUser.toDto(me));
    }

    @Override
    public ResponseEntity<UserOneResponse> updateUser(MultipartFile imageFile, String email) {
        User user = userService.updateAvatar(imageFile, email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(200).body(MapperUser.toDto(user));
    }

    @Override
    public ResponseEntity<Map<String, Object>> updatePassword(String id, ChangePasswordRequest request) {
        Map<String, Object> responses = userService.updatePassword(id, request);

        if (responses.get(MESSAGE_KEY).equals(USER_NOT_FOUND_MESSAGE) || responses.get(MESSAGE_KEY).equals("Current password is incorrect")) {
            return ResponseEntity.status(400).body(responses);
        }
        return ResponseEntity.status(200).body(responses);
    }
}