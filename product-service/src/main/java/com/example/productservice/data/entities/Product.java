package com.example.productservice.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
@Builder
public class Product {

    @Id
    String id;

    @Field
    String name;

    @Field
    String description;

    @Field
    Double price;

    @Field
    Integer quantity;

    @Field
    String userId;

}
