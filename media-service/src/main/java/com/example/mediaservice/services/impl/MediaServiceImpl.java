package com.example.mediaservice.services.impl;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.data.repositories.MediaRepository;
import com.example.mediaservice.services.MediaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public List<Media> getAllMedias() {
        return mediaRepository.findAll();
    }

    @Override
    public Media getMediaById(String id) {
        return mediaRepository.findById(id).orElse(null);
    }

    @Override
    public Media saveMedia(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public Media deleteMedia(Media media) {
        mediaRepository.delete(media);
        return media;
    }

    @Override
    public Media updateMedia(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public List<Media> getByProductId(String productId) {
        return mediaRepository.getAllByProductId(productId);
    }

    @Override
    public void deleteMediaByProductId(String productId) {
        mediaRepository.deleteMediaByProductId(productId);
    }

    @Override
    public void deleteByImagePath(String imagePath) {
        mediaRepository.deleteMediaByImagePath(imagePath);
    }

}
