package com.example.mediaservice.messaging;

import com.example.mediaservice.services.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductEventConsumer {
    private final MediaService mediaService;

    public ProductEventConsumer(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @KafkaListener(topics = "delete-product-media")
    public void deleteProduct(String productId) {
        log.info("Received message:");
        log.info("Delete media by product id: {}", productId);
        mediaService.deleteMediaByProductId(productId);
    }
}
