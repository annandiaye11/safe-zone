package com.example.mediaservice.services.impl;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.data.repositories.MediaRepository;
import com.example.mediaservice.services.MediaService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
        return  mediaRepository.save(media);
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
    public Media getByProductId(String productId) {
        return mediaRepository.findByProductId(productId).orElse(null);
    }

    @Override
    public void deleteMediaByProductId(String productId) {
        mediaRepository.deleteMediaByProductId(productId);
    }

}
