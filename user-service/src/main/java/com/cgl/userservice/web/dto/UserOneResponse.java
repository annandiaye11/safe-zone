package com.cgl.userservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserOneResponse {
    String id;
    String name;
    String email;
    String role;
    String avatar;
}
