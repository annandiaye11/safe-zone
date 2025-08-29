package com.cgl.userservice.web.dto;

import lombok.*;

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