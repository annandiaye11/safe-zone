package com.example.productservice.web.controllers;

import com.example.productservice.web.dto.ProductDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/products")
public interface ProductController {

    @PostMapping
    ResponseEntity<ProductDto> create(@RequestBody @Valid ProductDto productDto);

    @GetMapping
    ResponseEntity<List<ProductDto>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getById(@PathVariable("id") String id);

    @PutMapping("/{id}")
    ResponseEntity<ProductDto> update(@PathVariable("id") String id, @RequestBody @Valid ProductDto productDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable("id") String id);

    @GetMapping("/{userId}/user")
    ResponseEntity<List<ProductDto>> getByUserId(@PathVariable("userId") String userId);
}
