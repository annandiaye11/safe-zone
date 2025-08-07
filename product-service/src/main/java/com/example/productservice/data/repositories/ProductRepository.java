package com.example.productservice.data.repositories;

import com.example.productservice.data.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);

    void deleteProductByUserId(String userId);

    void deleteProductsByUserId(String userId);

    Product getProductByUserId(String userId);
}
