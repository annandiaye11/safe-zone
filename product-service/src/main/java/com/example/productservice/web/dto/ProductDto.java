package com.example.productservice.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private String id;

    @NotBlank(message = "Name must not be blank")
    @NotNull(message = "Name must not be null")
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.'\"-]*$", message = "Name can only contain alphanumeric characters, spaces, commas, periods, apostrophes, quotes, and hyphens")
    private String name;

    @NotBlank(message = "Description must not be blank")
    @NotNull(message = "Description must not be null")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Product price must not be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Product price must be at least 0.01")
    @Positive(message = "Product price must be positive")
    private Double price;

    @NotNull
    @Positive(message = "quantity must be positive")
    private Integer quantity;

    @NotNull(message = "User ID must not be blank")
    private String userId;

}
