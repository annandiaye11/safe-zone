package com.example.productservice.service;

import org.springframework.stereotype.Service;


public interface ProductEventPublisher {
    void sendDeleteEvent(String productId);
}
