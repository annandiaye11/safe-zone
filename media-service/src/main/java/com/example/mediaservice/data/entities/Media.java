package com.example.mediaservice.data.entities;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medias")
public class Media {
    @Id
    private String id;
    private String imagePath;
    private String productId;
}
