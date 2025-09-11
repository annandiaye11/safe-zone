package com.cgl.userservice.services;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.web.dto.ChangePasswordRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> getAllUsers();

    User getById(String id);

    User getByEmail(String email);

    User create(User user);

    User update(User user);

    User delete(User user);

    User getCurrentUser();

    User updateAvatar(MultipartFile imageFile, String userId);

    Map<String, Object> updatePassword(String id, ChangePasswordRequest request);
}
