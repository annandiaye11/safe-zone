package com.cgl.userservice.services;

import com.cgl.userservice.data.entities.User;

public interface UserService {
    User getById(String id);
    User getByEmail(String email);
    User create(User user);
    User update(String id,User user);
    boolean delete(String id);
}
