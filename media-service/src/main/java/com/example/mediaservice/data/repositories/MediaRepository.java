package com.example.mediaservice.data.repositories;

import com.example.mediaservice.data.entities.Media;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends MongoRepository<Media, String> {
    Optional<Media> findByProductId(String productId);

    List<Media> getAllByProductId(String productId);

    void deleteMediaByProductId(String productId);

    void deleteMediaByImagePath(String imagePath);
}
