package com.example.mediaservice.services;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.data.repositories.MediaRepository;
import com.example.mediaservice.services.impl.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private Media testMedia;

    @BeforeEach
    void setUp() {
        testMedia = new Media();
        testMedia.setId("1");
        testMedia.setImagePath("/uploads/image1.jpg");
        testMedia.setProductId("product123");
    }

    @Test
    void testGetAllMedias_Success() {
        // Given
        Media media2 = new Media();
        media2.setId("2");
        media2.setImagePath("/uploads/image2.jpg");
        media2.setProductId("product456");

        List<Media> medias = Arrays.asList(testMedia, media2);
        when(mediaRepository.findAll()).thenReturn(medias);

        // When
        List<Media> result = mediaService.getAllMedias();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(1).getId()).isEqualTo("2");
        verify(mediaRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMedias_EmptyList() {
        // Given
        when(mediaRepository.findAll()).thenReturn(List.of());

        // When
        List<Media> result = mediaService.getAllMedias();

        // Then
        assertThat(result).isEmpty();
        verify(mediaRepository, times(1)).findAll();
    }

    @Test
    void testGetMediaById_Found() {
        // Given
        when(mediaRepository.findById("1")).thenReturn(Optional.of(testMedia));

        // When
        Media result = mediaService.getMediaById("1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getImagePath()).isEqualTo("/uploads/image1.jpg");
        verify(mediaRepository, times(1)).findById("1");
    }

    @Test
    void testGetMediaById_NotFound() {
        // Given
        when(mediaRepository.findById("999")).thenReturn(Optional.empty());

        // When
        Media result = mediaService.getMediaById("999");

        // Then
        assertThat(result).isNull();
        verify(mediaRepository, times(1)).findById("999");
    }

    @Test
    void testSaveMedia_Success() {
        // Given
        when(mediaRepository.save(any(Media.class))).thenReturn(testMedia);

        // When
        Media result = mediaService.saveMedia(testMedia);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getImagePath()).isEqualTo("/uploads/image1.jpg");
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    @Test
    void testUpdateMedia_Success() {
        // Given
        testMedia.setImagePath("/uploads/updated-image.jpg");
        when(mediaRepository.save(any(Media.class))).thenReturn(testMedia);

        // When
        Media result = mediaService.updateMedia(testMedia);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImagePath()).isEqualTo("/uploads/updated-image.jpg");
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    @Test
    void testDeleteMedia_Success() {
        // Given
        doNothing().when(mediaRepository).delete(any(Media.class));

        // When
        Media result = mediaService.deleteMedia(testMedia);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        verify(mediaRepository, times(1)).delete(any(Media.class));
    }

    @Test
    void testGetByProductId_Found() {
        // Given
        Media media2 = new Media();
        media2.setId("2");
        media2.setProductId("product123");

        List<Media> productMedias = Arrays.asList(testMedia, media2);
        when(mediaRepository.getAllByProductId("product123")).thenReturn(productMedias);

        // When
        List<Media> result = mediaService.getByProductId("product123");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo("product123");
        assertThat(result.get(1).getProductId()).isEqualTo("product123");
        verify(mediaRepository, times(1)).getAllByProductId("product123");
    }

    @Test
    void testGetByProductId_EmptyList() {
        // Given
        when(mediaRepository.getAllByProductId("product999")).thenReturn(List.of());

        // When
        List<Media> result = mediaService.getByProductId("product999");

        // Then
        assertThat(result).isEmpty();
        verify(mediaRepository, times(1)).getAllByProductId("product999");
    }

    @Test
    void testDeleteMediaByProductId_Success() {
        // Given
        doNothing().when(mediaRepository).deleteMediaByProductId("product123");

        // When
        mediaService.deleteMediaByProductId("product123");

        // Then
        verify(mediaRepository, times(1)).deleteMediaByProductId("product123");
    }

    @Test
    void testDeleteByImagePath_Success() {
        // Given
        String imagePath = "/uploads/image1.jpg";
        doNothing().when(mediaRepository).deleteMediaByImagePath(imagePath);

        // When
        mediaService.deleteByImagePath(imagePath);

        // Then
        verify(mediaRepository, times(1)).deleteMediaByImagePath(imagePath);
    }
}

