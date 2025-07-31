package com.example.mediaservice.web.controllers.impl;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.services.MediaService;
import com.example.mediaservice.utils.mappers.MapperMedia;
import com.example.mediaservice.web.controllers.MediaController;
import com.example.mediaservice.web.dto.requests.MediaDtoAll;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class MediaControllerImpl implements MediaController {
    private final MediaService mediaService;
    public MediaControllerImpl(MediaService mediaService) {
        this.mediaService = mediaService;
    }
    @Override
    public ResponseEntity<Map<String, Object>> getAllMedias() {
        HashMap<String, Object> response = new HashMap<>();
        List<Media> medias = mediaService.getAllMedias();
        if (medias.isEmpty()) {
            response.put("message", "No medias found");
            return ResponseEntity.status(404).body(response);
        }
        response.put("message", "Medias found");
        response.put("medias", medias);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getMediaById(String id) {
        HashMap<String, Object> response = new HashMap<>();
        Media media = mediaService.getMediaById(id);
        return getMapResponseEntity(response, media);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getMediaByProductId(String id) {
        HashMap<String, Object> response = new HashMap<>();
        Media media = mediaService.getByProductId(id);
        return getMapResponseEntity(response, media);
    }

    private ResponseEntity<Map<String, Object>> getMapResponseEntity(HashMap<String, Object> response, Media media) {
        if (media == null) {
            response.put("message", "Media not found");
            return ResponseEntity.status(404).body(response);
        }
        response.put("message", "Media found");
        response.put("media", media);

        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createMedia(MediaDtoAll mediaDtoAll) {
        HashMap<String, Object> response = new HashMap<>();
        Media media = MapperMedia.toEntity(mediaDtoAll);
        Media mediaSaved = mediaService.saveMedia(media);
        response.put("message", "Media created");
        response.put("media", mediaSaved);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateMedia(String id, MediaDtoAll mediaDtoAll) {
        Media media = mediaService.getMediaById(id);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        media.setImagePath(mediaDtoAll.getImagePath());
        media.setProductId(mediaDtoAll.getProductId());
        Media mediaCreated = mediaService.updateMedia(media);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Media updated");
        response.put("media", mediaCreated);
        return ResponseEntity.status(200).body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteMedia(String id) {
        Media media = mediaService.getMediaById(id);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        Media mediaDeleted = mediaService.deleteMedia(media);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Media deleted");
        response.put("media", mediaDeleted);
        return ResponseEntity.status(200).body(response);
    }
}
