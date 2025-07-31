package com.cgl.userservice.services;

import com.cgl.userservice.data.entities.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getById(String id);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    User delete(User user);
}
