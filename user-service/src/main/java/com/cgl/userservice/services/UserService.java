package com.cgl.userservice.services;

import com.cgl.userservice.data.entities.User;

public interface UserService {
    User getById(String id);
    User getByEmail(String email);
    User create(User user);
    User update(User user);
    User delete(String id);
}
