package com.example.productservice.service;

public interface ProductEventPublisher {
    void sendDeleteEvent(String productId);
}
