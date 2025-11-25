package com.cgl.userservice.data.repositories;

import com.cgl.userservice.data.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByName(String name);
}
