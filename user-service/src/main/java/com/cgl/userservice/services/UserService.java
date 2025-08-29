package com.cgl.userservice.services;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.web.dto.ChangePasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getById(String id);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    User delete(User user);
    User getCurrentUser();
    User updateAvatar(MultipartFile imageFile, String userId);
    void updatePassword(String id, ChangePasswordRequest request);
}
