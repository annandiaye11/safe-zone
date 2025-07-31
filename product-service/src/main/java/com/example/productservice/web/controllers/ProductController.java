package com.example.productservice.web.controllers;

import com.example.productservice.web.dto.ProductBasicDTO;
import com.example.productservice.web.dto.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/products")
public interface ProductController {

    @PostMapping
    ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductBasicDTO productBasicDTO);

    @GetMapping
    ResponseEntity<List<ProductResponseDTO>> getAll();

    @GetMapping("/{id}")
    ResponseEntity<ProductResponseDTO> getById(@PathVariable String id);

    @PutMapping("/{id}")
    ResponseEntity<ProductResponseDTO> update(@PathVariable String id,
                                              @RequestBody @Valid ProductBasicDTO productBasicDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id);
}
