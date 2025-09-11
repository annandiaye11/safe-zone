package com.cgl.userservice.web.controllers;

import com.cgl.userservice.web.dto.ChangePasswordRequest;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RequestMapping("/api/v1/users")
public interface UserController {
    @GetMapping("")
    ResponseEntity<Map<String, Object>> getAllUsers();

    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id);

    //@PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}")
    ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody @Valid UserDto userDto);

    // @PreAuthorize("#id ==  authentication.principal.id")
    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id);

    @GetMapping("/me")
    ResponseEntity<UserOneResponse> getCurrentUser();

    @PatchMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserOneResponse> updateUser(@RequestParam("imagePath") MultipartFile imageFile, @RequestParam("userId") String userId);

    @PatchMapping("/{id}/changePassword")
    ResponseEntity<Map<String, Object>> updatePassword(@PathVariable("id") String id,
                                                       @Valid @RequestBody ChangePasswordRequest request
    );

}
