package com.example.mediaservice.services;

import com.example.mediaservice.data.entities.Media;

import java.util.List;

public interface MediaService {
    List<Media> getAllMedias();

    Media getMediaById(String id);

    Media saveMedia(Media media);

    Media deleteMedia(Media media);

    Media updateMedia(Media media);

    List<Media> getByProductId(String productId);

    void deleteMediaByProductId(String productId);

    void deleteByImagePath(String imagePath);

}
