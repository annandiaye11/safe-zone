package com.cgl.userservice.services.impl;

import com.cgl.userservice.services.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventPublisherImpl implements UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendDeleteEvent(String userId) {
        kafkaTemplate.send("delete-user-products", userId);
    }
}
