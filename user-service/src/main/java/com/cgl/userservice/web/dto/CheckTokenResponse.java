package com.cgl.userservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckTokenResponse {
    private boolean valid;
    private String message;

    public CheckTokenResponse(boolean valid) {
        this.valid = valid;
        this.message = valid ? "Valid token" : "Invalid token";
    }
}
