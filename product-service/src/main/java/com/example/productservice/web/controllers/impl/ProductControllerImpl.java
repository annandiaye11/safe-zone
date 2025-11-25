package com.example.productservice.web.controllers.impl;

import com.example.productservice.data.entities.Product;
import com.example.productservice.service.ProductService;
import com.example.productservice.web.controllers.ProductController;
import com.example.productservice.web.dto.ProductDto;
import com.example.productservice.web.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {
    private final ProductService productService;

    @Override
    public ResponseEntity<ProductDto> create(@RequestBody @Valid ProductDto productDto) {
        Product product = ProductMapper.toEntity(productDto);
        if (productService.getByName(product.getName()) != null) {
            return ResponseEntity.badRequest().body(null);
        }
        Product productSaved = productService.create(product);
        productDto.setId(productSaved.getId());
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<ProductDto>> getAll() {
        List<Product> products = productService.getAllProducts();
        return getListResponseEntity(products);
    }

    private ResponseEntity<List<ProductDto>> getListResponseEntity(List<Product> products) {
        /*if (products == null || products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }*/
        // Return emptyList if no products found
        if (products.isEmpty()) {
            return ResponseEntity.ok().body(List.of());
        }

        List<ProductDto> productDtos = products.stream()
                .map(ProductMapper::toDto)
                .toList();
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDto> getById(@PathVariable("id") String id) {
        Product product = productService.getById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        ProductDto productDto = ProductMapper.toDto(product);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDto> update(@PathVariable("id") String id, @RequestBody @Valid ProductDto productDto) {
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        productDto.setId(id);
        Product product = ProductMapper.toEntity(productDto);
        productService.update(product);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        productService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ProductDto>> getByUserId(@PathVariable("userId") String userId) {
        List<Product> products = productService.getByUserId(userId);
        return getListResponseEntity(products);
    }

}
