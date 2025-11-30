package com.cgl.userservice.web.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class ChangePasswordResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public ChangePasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = null;
    }

    public ChangePasswordResponse(boolean success, String message, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
    }
}
