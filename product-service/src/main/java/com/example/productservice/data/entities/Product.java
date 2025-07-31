package com.example.productservice.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "product")
public class Product {

    @Id
    String id;
    @Field("name")
    String name;
    @Field
    String description;
    @Field
    Double price;
    @Field
    Integer quantity;
    @Field
    String userId;
public Product(String name, String description, Double price, Integer quantity, String userId) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.quantity = quantity;
}
}
