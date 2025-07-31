package com.cgl.userservice.web.controllers.impl;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.services.UserService;
import com.cgl.userservice.utils.JwtTools;
import com.cgl.userservice.utils.mapper.MapperUser;
import com.cgl.userservice.web.controllers.AuthController;
import com.cgl.userservice.web.dto.RegisterResponse;
import com.cgl.userservice.web.dto.RequestDto;
import com.cgl.userservice.web.dto.ResponseDto;
import com.cgl.userservice.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class AuthControllerImpl implements AuthController {

    private final UserService userService;
    private final JwtTools jwtTools;
    private final PasswordEncoder passwordEncoder;

    public AuthControllerImpl(UserService userService, JwtTools jwtTools, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTools = jwtTools;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<ResponseDto> login(RequestDto userLoginDTO) {
        System.out.println("\n*****************START login");
        User checkUser = userService.getByEmail(userLoginDTO.getEmail());

        if (checkUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email");
        }

        System.out.println("userLoginDTO: " + userLoginDTO);
        System.out.println("user password: '" + checkUser.getPassword() + "'");
        System.out.println("dto password: '" + userLoginDTO.getPassword() + "'");

        if (passwordEncoder.matches(userLoginDTO.getPassword(), checkUser.getPassword())) {
            String token = jwtTools.generateToken(checkUser);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .body(new ResponseDto(token));
        } else {
            System.out.println("FAILED !!!");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> register(UserDto userRegisterDTO) {
        System.out.println("\n********************START register");
        Map<String, Object> response = new HashMap<>();
        log.info("User register: {}", userRegisterDTO.getEmail());
        if (userService.getByEmail(userRegisterDTO.getEmail()) != null) {
            response.put("message", "User already exists");
            return ResponseEntity.status(400).body(response);
        }
        log.info("User register: {}", userRegisterDTO);
        RegisterResponse registerResponse = new RegisterResponse(userService.create(MapperUser.toEntity(userRegisterDTO)));
        response.put("message", "User created");
        response.put("response", registerResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(response);
    }
}
