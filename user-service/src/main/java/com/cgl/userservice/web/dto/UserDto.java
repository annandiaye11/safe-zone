package com.cgl.userservice.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;

    @Size(min = 3, max = 100, message = "The name should be have ")
    private String name;

}
