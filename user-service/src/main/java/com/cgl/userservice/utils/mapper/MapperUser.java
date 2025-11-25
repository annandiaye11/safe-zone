package com.cgl.userservice.utils.mapper;

import com.cgl.userservice.data.entities.User;
import com.cgl.userservice.data.enums.Role;
import com.cgl.userservice.web.dto.UserAllResponse;
import com.cgl.userservice.web.dto.UserDto;
import com.cgl.userservice.web.dto.UserOneResponse;

public class MapperUser {

    public static User toEntity(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(Role.getRole(userDto.getRole()));
        user.setAvatar(userDto.getAvatar());
        return user;
    }

    public static UserOneResponse toDto(User user) {
        UserOneResponse userOneResponse = new UserOneResponse();
        userOneResponse.setId(user.getId());
        userOneResponse.setEmail(user.getEmail());
        userOneResponse.setName(user.getName());
        userOneResponse.setAvatar(user.getAvatar());
        userOneResponse.setRole(String.valueOf(user.getRole()));
        return userOneResponse;
    }

    public static UserAllResponse toDtoSecond(User user) {
        UserAllResponse userAllResponse = new UserAllResponse();
        userAllResponse.setId(user.getId());
        userAllResponse.setEmail(user.getEmail());
        userAllResponse.setName(user.getName());
        userAllResponse.setRole(user.getRole().name());
        return userAllResponse;
    }
}
