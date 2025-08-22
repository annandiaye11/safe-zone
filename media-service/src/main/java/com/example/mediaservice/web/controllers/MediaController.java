package com.example.mediaservice.web.controllers;

import com.example.mediaservice.web.dto.requests.MediaDtoAll;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/media")
public interface MediaController {
    @GetMapping("")
    ResponseEntity<Map<String, Object>> getAllMedias();

    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getMediaById(@PathVariable String id);

    @GetMapping("/product/{id}")
    ResponseEntity<Map<String, Object>> getMediaByProductId(@PathVariable String id);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Map<String, Object>> createMedia( @RequestParam("imagePath") List<MultipartFile> imageFile,
                                                     @RequestParam("productId") String productId);

    //@PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}")
    ResponseEntity<Map<String, Object>> updateMedia(@PathVariable String id, @RequestBody @Valid MediaDtoAll mediaDtoAll);

    // @PreAuthorize("#id ==  authentication.principal.id")
    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, Object>> deleteMedia(@PathVariable String id);
}
