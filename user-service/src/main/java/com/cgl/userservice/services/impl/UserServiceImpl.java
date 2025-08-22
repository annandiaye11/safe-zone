package com.cgl.userservice.services.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.repositories.UserRepository;
import com.cgl.userservice.services.UserEventPublisher;
import com.cgl.userservice.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
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

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            throw new RuntimeException("User is not authenticated");
        }
    }

}
