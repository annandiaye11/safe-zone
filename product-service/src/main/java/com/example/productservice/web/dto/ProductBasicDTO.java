package com.example.productservice.web.dto;

import com.example.productservice.data.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBasicDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @PositiveOrZero(message = "Price must be greater or equals to 0")
    private Double price;

    @NotNull
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    public Product toProduct(String userId) {
        return new Product(name, description, price, quantity, userId);
    }
}
