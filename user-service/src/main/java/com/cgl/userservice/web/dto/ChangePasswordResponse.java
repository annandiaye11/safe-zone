package com.cgl.userservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public ChangePasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}