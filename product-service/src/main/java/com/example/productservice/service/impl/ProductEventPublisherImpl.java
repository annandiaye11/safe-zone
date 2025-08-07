package com.example.productservice.service.impl;

import com.example.productservice.service.ProductEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventPublisherImpl implements ProductEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProductEventPublisherImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendDeleteEvent(String productId) {
        kafkaTemplate.send("delete-product-media", productId);
    }
}
