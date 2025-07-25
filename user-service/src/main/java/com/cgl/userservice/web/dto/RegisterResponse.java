package com.cgl.userservice.web.dto;

import lombok.*;
import com.cgl.userservice.data.entities.User;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private int status;
    private String message;
    private String id;
    private String name;
    private String email;

    public RegisterResponse (User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
