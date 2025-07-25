package com.cgl.userservice.web.controllers;

import com.cgl.userservice.web.dto.RequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public interface UserController {
    @GetMapping("")
    ResponseEntity<Map<String, Object>> getAllUsers();

    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id);

    @PostMapping("/register")
    ResponseEntity<Map<String, Object>> createUser(@RequestBody @Valid RequestDto requestDto);


    @PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}")
    ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody @Valid RequestDto requestDto);

    @PreAuthorize("#id ==  authentication.principal.id")
    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id);


}
