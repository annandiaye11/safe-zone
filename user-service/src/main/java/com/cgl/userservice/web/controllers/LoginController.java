package com.cgl.userservice.web.controllers;

import com.cgl.userservice.web.dto.RequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/api/login")
public interface LoginController {
    @PostMapping("")
    ResponseEntity<Map<String, Object>> login(@RequestBody @Valid RequestDto requestDto);
}