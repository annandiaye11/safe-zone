package com.cgl.userservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAllResponse {
    String id;
    String name;
    String email;
    String role;
    List<String> products;
}
