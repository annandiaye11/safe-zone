package com.example.productservice.service.impl;

import com.example.productservice.data.entities.Product;
import com.example.productservice.data.repositories.ProductRepository;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product update(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getByUserId(String userId) {
        List<Product> products = productRepository.findByUserId(userId);
        if (products.isEmpty()) {
            return null;
        }
        return products;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return List.of();
        }
        return productRepository.findAll();
    }

    @Override
    public void delete(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public void deleteProductsByUser(String userId) {
        productRepository.deleteProductsByUserId(userId);
    }

    @Override
    public Product getByName(String name) {
        return productRepository.getByName(name);
    }
}
