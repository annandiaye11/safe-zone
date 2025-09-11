package com.cgl.userservice.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {
    private String token;
    private String email;

    public ResponseDto(String token) {
        this.token = token;
    }
}
