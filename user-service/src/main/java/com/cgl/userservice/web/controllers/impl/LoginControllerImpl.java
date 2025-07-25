package com.cgl.userservice.web.controllers.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.services.LoginService;
import com.cgl.userservice.utils.JwtTools;
import com.cgl.userservice.web.controllers.LoginController;
import com.cgl.userservice.web.dto.RequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class LoginControllerImpl implements LoginController {
    private final LoginService loginService;
    private final JwtTools jwtTools;
    private final PasswordEncoder passwordEncoder;

    public LoginControllerImpl(LoginService loginService, JwtTools jwtTools, PasswordEncoder passwordEncoder) {
        this.loginService = loginService;
        this.jwtTools = jwtTools;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public ResponseEntity<Map<String, Object>> login(RequestDto requestDto) {
        UserDetails userDetails = loginService.loadUserByUsername(requestDto.getEmail());
        User user = (User) userDetails;
        Map<String, Object> response = new HashMap<>();
        if (user != null && passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            String token = jwtTools.generateToken(user);
            response.put("token", token);
            response.put("Login", "Successful");
            return ResponseEntity.ok(response);
        }
        response.put("Login", "Failed");
        response.put("message", "Email or password is incorrect");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
