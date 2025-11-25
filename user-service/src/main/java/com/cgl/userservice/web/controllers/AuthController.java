package com.cgl.userservice.web.controllers;

import com.cgl.userservice.web.dto.RequestDto;
import com.cgl.userservice.web.dto.ResponseDto;
import com.cgl.userservice.web.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<ResponseDto> login(@Valid @RequestBody RequestDto userLoginDTO);

    @PostMapping("/register")
    ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserDto userRegisterDTO);
}
