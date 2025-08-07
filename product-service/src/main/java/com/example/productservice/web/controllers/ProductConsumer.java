package com.example.productservice.web.controllers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public interface ProductConsumer {

    @KafkaListener(topics = "delete-user")
    void deleteUser(String userId);
}
