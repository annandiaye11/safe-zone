package com.example.productservice.service;

import com.example.productservice.data.entities.Product;

import java.util.List;

public interface ProductService {
    Product create(Product product);

    Product getById(String id);

    Product update(Product product);

    List<Product> getByUserId(String userId);

    List<Product> getAllProducts();

    void delete(String id);

    void deleteProductsByUser(String userId);

    Product getByName(String name);
}
