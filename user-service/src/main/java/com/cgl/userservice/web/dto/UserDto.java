package com.cgl.userservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "The name should be have least 3 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Size(min = 6, max = 100, message = "The email should be have least 6 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password should be have least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Size(min = 6, message = "The role should be SELLER or CLIENT")
    private String role;

    private String avatar;


}
