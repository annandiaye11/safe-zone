package com.cgl.userservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.cgl.userservice.data.repositories.UserRepository;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }
}

