package com.cgl.userservice.web.controllers;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.web.dto.RegisterResponse;
import com.cgl.userservice.web.dto.RequestDto;
import com.cgl.userservice.web.dto.ResponseDto;
import com.cgl.userservice.web.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<ResponseDto> login(@Valid @RequestBody RequestDto userLoginDTO);

    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(@Valid @RequestBody UserDto userRegisterDTO);
}
