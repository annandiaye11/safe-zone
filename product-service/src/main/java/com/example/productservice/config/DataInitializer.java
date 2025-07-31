package com.example.productservice.config;

import com.example.productservice.data.entities.Product;
import com.example.productservice.data.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final ProductRepository productRepository;
    @Bean
    public CommandLineRunner init() {
        return args -> {
            if (productRepository.count() !=0){
                productRepository.deleteAll();
            } else {
                 Product product =  new Product();
                 product.setName("Savon");
                 product.setDescription("Savon pour le corps");
                 product.setPrice(1500.0);
                 product.setQuantity(10);
                 product.setUserId("11");
                 productRepository.save(product);
            }
        };
    }
}
