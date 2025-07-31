package com.cgl.userservice.web.controllers.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.services.UserService;
import com.cgl.userservice.utils.mapper.MapperUser;
import com.cgl.userservice.web.controllers.UserController;
import com.cgl.userservice.web.dto.RequestDto;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public UserControllerImpl(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        if (users.isEmpty()) {
            response.put("message", "No users found");
            return ResponseEntity.status(404).body(response);
        }
        List<UserOneResponse> userResponses = users.stream()
                .map(MapperUser::toDto)
                .toList();
        response.put("message", "Users found");
        response.put("users", userResponses);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUserById(String id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getById(id);
        if (user == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
        response.put("message", "User found");
        response.put("user", MapperUser.toDtoSecond(user));
        return ResponseEntity.status(200).body(response);
    }


    @Override
    public ResponseEntity<Map<String, Object>> updateUser(String id, UserDto userDto) {
        User user = userService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAvatar(userDto.getAvatar());
        userService.update(user);
        response.put("message", "User updated");
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
        response.put("message", "User deleted");
        response.put("user", MapperUser.toDto(user));
        return ResponseEntity.status(200).body(response);
    }
}
