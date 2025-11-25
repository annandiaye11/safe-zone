package com.cgl.userservice.services;

import org.springframework.stereotype.Service;

@Service
public interface UserEventPublisher {
    void sendDeleteEvent(String userId);
}
