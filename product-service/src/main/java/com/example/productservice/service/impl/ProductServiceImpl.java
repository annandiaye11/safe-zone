package com.example.productservice.service.impl;

import com.example.productservice.data.entities.Product;
import com.example.productservice.data.repositories.ProductRepository;
import com.example.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Override
    public Product update(String id, Product product) {
        Product updateProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));


        if (!Objects.equals(updateProduct.getUserId(), product.getUserId())) {
            System.out.println("403-Forbidden (from updateProduct)");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can't update other's product");
        }

        updateProduct.setName(product.getName());
        updateProduct.setDescription(product.getDescription());
        updateProduct.setPrice(product.getPrice());
        productRepository.save(updateProduct);
        return updateProduct;
    }

    @Override
    public List<Product> getByUserId(String userId) {
        if (!productRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return productRepository.findByUserId(userId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public boolean delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        productRepository.delete(product);
        return true;
    }
}
