package com.cgl.userservice.services.impl;

import com.cgl.userservice.services.UserEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisherImpl implements UserEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserEventPublisherImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendDeleteEvent(String userId) {
        kafkaTemplate.send("delete-user-products", userId);
    }
}
