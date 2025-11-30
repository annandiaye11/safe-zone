package com.cgl.userservice.services.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.repositories.UserRepository;
import com.cgl.userservice.exception.UnauthorizedException;
import com.cgl.userservice.services.UserEventPublisher;
import com.cgl.userservice.services.UserService;
import com.cgl.userservice.web.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    static final String MESSAGE_KEY = "message";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;
    private final S3Service s3Service;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public User update(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public User delete(User user) {
        userRepository.delete(user);
        userEventPublisher.sendDeleteEvent(user.getId());
        return user;
    }

    @Override
    public User getCurrentUser() {
        String email = getCurrentUserEmail();

        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Override
    public User updateAvatar(MultipartFile imageFile, String userId) {
        User user = getById(userId);
        if (user == null) return null;
        String avatar = s3Service.uploadFile(imageFile);
        user.setAvatar(avatar);
        return userRepository.save(user);
    }

    @Override
    public Map<String, Object> updatePassword(String id, ChangePasswordRequest request) {
        HashMap<String, Object> response = new HashMap<>();

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            response.put(MESSAGE_KEY, "Cet utilisateur n'existe pas");
            return response;
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            response.put(MESSAGE_KEY, "Le mot de passe actuel n'est pas correcte");
            return response;
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
        response.put(MESSAGE_KEY, "Le mot de passe a été changé");
        response.put("user", user);
        return response;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            throw new UnauthorizedException("User is not authenticated");
        }
    }
}
