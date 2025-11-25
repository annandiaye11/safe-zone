package com.example.productservice.web.mapper;

import com.example.productservice.data.entities.Product;
import com.example.productservice.web.dto.ProductDto;

public class ProductMapper {

    public static Product toEntity(ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .quantity(productDto.getQuantity())
                .userId(productDto.getUserId())
                .build();
    }

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .userId(product.getUserId())
                .build();
    }

}
