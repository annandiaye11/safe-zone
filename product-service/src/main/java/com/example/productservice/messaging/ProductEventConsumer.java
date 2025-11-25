package com.example.productservice.messaging;

import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "delete-user-products")
    public void deleteProduct(String userId) {
        log.info("Received message:");
        log.info("Delete products for user: {}", userId);
        productService.deleteProductsByUser(userId);
    }

}
