package com.example.productservice.data.repositories;

import com.example.productservice.data.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByUserId(String userId);

    void deleteProductsByUserId(String userId);

    // List<Product> getProductsByUserId(String userId);

    Product getByName(String name);
}
