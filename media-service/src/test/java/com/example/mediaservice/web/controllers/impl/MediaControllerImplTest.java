package com.example.mediaservice.web.controllers.impl;

import com.example.common.services.S3Service;
import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.services.MediaService;
import com.example.mediaservice.web.dto.requests.MediaDtoAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaControllerImplTest {

    @Mock
    private MediaService mediaService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MediaControllerImpl mediaController;

    private Media testMedia;
    private List<Media> testMediaList;

    @BeforeEach
    void setUp() {
        testMedia = new Media();
        testMedia.setId("media-123");
        testMedia.setImagePath("https://s3.amazonaws.com/bucket/image.jpg");
        testMedia.setProductId("product-123");

        testMediaList = new ArrayList<>();
        testMediaList.add(testMedia);
    }

    @Test
    void testGetAllMedias_WithMedias_ReturnsMediasList() {
        // Given
        when(mediaService.getAllMedias()).thenReturn(testMediaList);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getAllMedias();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "Medias found");
        assertThat(response.getBody()).containsKey("medias");

        List<?> medias = (List<?>) response.getBody().get("medias");
        assertThat(medias).hasSize(1);

        verify(mediaService, times(1)).getAllMedias();
    }

    @Test
    void testGetAllMedias_NoMedias_Returns404() {
        // Given - Empty list (Condition: medias.isEmpty())
        when(mediaService.getAllMedias()).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getAllMedias();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "No medias found");
        assertThat(response.getBody()).doesNotContainKey("medias");

        verify(mediaService, times(1)).getAllMedias();
    }

    @Test
    void testGetAllMedias_MultipleMedias() {
        // Given - Multiple medias
        Media media2 = new Media();
        media2.setId("media-456");
        media2.setImagePath("image2.jpg");
        media2.setProductId("product-456");

        List<Media> multipleMedias = List.of(testMedia, media2);
        when(mediaService.getAllMedias()).thenReturn(multipleMedias);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getAllMedias();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<?> medias = (List<?>) response.getBody().get("medias");
        assertThat(medias).hasSize(2);
    }


    @Test
    void testGetMediaById_MediaExists_ReturnsMedia() {
        // Given
        when(mediaService.getMediaById("media-123")).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaById("media-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Media found");
        assertThat(response.getBody()).containsKey("media");

        verify(mediaService, times(1)).getMediaById("media-123");
    }

    @Test
    void testGetMediaById_MediaNotFound_Returns404() {
        // Given - Media not found (Condition: media == null)
        when(mediaService.getMediaById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaById("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "Media not found");
        assertThat(response.getBody()).doesNotContainKey("media");

        verify(mediaService, times(1)).getMediaById("non-existent");
    }

    @Test
    void testGetMediaByProductId_MediaExists_ReturnsMediaList() {
        // Given
        when(mediaService.getByProductId("product-123")).thenReturn(testMediaList);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaByProductId("product-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Media found");
        assertThat(response.getBody()).containsKey("media");

        List<?> medias = (List<?>) response.getBody().get("media");
        assertThat(medias).hasSize(1);

        verify(mediaService, times(1)).getByProductId("product-123");
    }

    @Test
    void testGetMediaByProductId_MediaNotFound_Returns404() {
        // Given - Media not found (Condition: media == null)
        when(mediaService.getByProductId("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaByProductId("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "Media not found");

        verify(mediaService, times(1)).getByProductId("non-existent");
    }

    @Test
    void testGetMediaByProductId_MultipleMedias() {
        // Given - Multiple medias for same product
        Media media2 = new Media();
        media2.setId("media-789");
        media2.setImagePath("image3.jpg");
        media2.setProductId("product-123");

        List<Media> multipleMedias = List.of(testMedia, media2);
        when(mediaService.getByProductId("product-123")).thenReturn(multipleMedias);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaByProductId("product-123");

        // Then
        List<?> medias = (List<?>) response.getBody().get("media");
        assertThat(medias).hasSize(2);
    }

    @Test
    void testCreateMedia_SingleFile_Success() {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file);

        when(s3Service.uploadFile(file)).thenReturn("uploaded-path.jpg");
        when(mediaService.saveMedia(any(Media.class))).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.createMedia(files, "product-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("message", "Media created");
        assertThat(response.getBody()).containsKey("media");

        verify(s3Service, times(1)).uploadFile(file);
        verify(mediaService, times(1)).saveMedia(any(Media.class));
    }

    @Test
    void testCreateMedia_MultipleFiles_Success() {
        // Given - 3 files (maximum allowed)
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        MultipartFile file3 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2, file3);

        when(s3Service.uploadFile(any())).thenReturn("path1.jpg", "path2.jpg", "path3.jpg");
        when(mediaService.saveMedia(any(Media.class))).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.createMedia(files, "product-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<?> medias = (List<?>) response.getBody().get("media");
        assertThat(medias).hasSize(3);

        verify(s3Service, times(3)).uploadFile(any());
        verify(mediaService, times(3)).saveMedia(any(Media.class));
    }

    @Test
    void testCreateMedia_TooManyFiles_ReturnsBadRequest() {
        // Given - More than 3 files (Condition: imageFile.size() > 3)
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        MultipartFile file3 = mock(MultipartFile.class);
        MultipartFile file4 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2, file3, file4);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.createMedia(files, "product-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "You can't upload more than 3 images");

        verify(s3Service, never()).uploadFile(any());
        verify(mediaService, never()).saveMedia(any());
    }

    @Test
    void testCreateMedia_TwoFiles() {
        // Given - 2 files
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);

        when(s3Service.uploadFile(any())).thenReturn("path1.jpg", "path2.jpg");
        when(mediaService.saveMedia(any(Media.class))).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.createMedia(files, "product-456");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<?> medias = (List<?>) response.getBody().get("media");
        assertThat(medias).hasSize(2);

        verify(s3Service, times(2)).uploadFile(any());
    }

    @Test
    void testUpdateMedia_MediaExists_UpdatesMedia() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("new-product-id");

        Media updatedMedia = new Media();
        updatedMedia.setId("media-123");
        updatedMedia.setProductId("new-product-id");

        when(mediaService.getMediaById("media-123")).thenReturn(testMedia);
        when(mediaService.updateMedia(any(Media.class))).thenReturn(updatedMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.updateMedia("media-123", mediaDtoAll);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Media updated");
        assertThat(response.getBody()).containsKey("media");

        verify(mediaService, times(1)).getMediaById("media-123");
        verify(mediaService, times(1)).updateMedia(any(Media.class));
    }

    @Test
    void testUpdateMedia_MediaNotFound_Returns404() {
        // Given - Media not found (Condition: media == null)
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-id");

        when(mediaService.getMediaById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.updateMedia("non-existent", mediaDtoAll);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(mediaService, times(1)).getMediaById("non-existent");
        verify(mediaService, never()).updateMedia(any());
    }

    @Test
    void testDeleteMedia_MediaExists_DeletesMedia() {
        // Given
        when(mediaService.getMediaById("media-123")).thenReturn(testMedia);
        when(mediaService.deleteMedia(testMedia)).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.deleteMedia("media-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Media deleted");
        assertThat(response.getBody()).containsKey("media");

        verify(mediaService, times(1)).getMediaById("media-123");
        verify(mediaService, times(1)).deleteMedia(testMedia);
    }

    @Test
    void testDeleteMedia_MediaNotFound_Returns404() {
        // Given - Media not found (Condition: media == null)
        when(mediaService.getMediaById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.deleteMedia("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(mediaService, times(1)).getMediaById("non-existent");
        verify(mediaService, never()).deleteMedia(any());
    }

    @Test
    void testDeleteByImagePath_MediaExists_DeletesMedia() {
        // Given
        String imagePath = "https://s3.amazonaws.com/bucket/image.jpg";
        when(mediaService.getAllMedias()).thenReturn(testMediaList);
        when(mediaService.deleteMedia(testMedia)).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.deleteByImagePath(imagePath);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "This media has been deleted");
        assertThat(response.getBody()).containsKey("media");

        verify(mediaService, times(1)).getAllMedias();
        verify(mediaService, times(1)).deleteMedia(testMedia);
    }

    @Test
    void testDeleteByImagePath_MediaNotFound_Returns404() {
        // Given - Media with path not found (Condition: media == null in filter)
        String imagePath = "non-existent-path.jpg";
        when(mediaService.getAllMedias()).thenReturn(testMediaList);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.deleteByImagePath(imagePath);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(mediaService, times(1)).getAllMedias();
        verify(mediaService, never()).deleteMedia(any());
    }

    @Test
    void testDeleteByImagePath_EmptyMediaList() {
        // Given - No medias in database
        when(mediaService.getAllMedias()).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.deleteByImagePath("any-path.jpg");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(mediaService, times(1)).getAllMedias();
        verify(mediaService, never()).deleteMedia(any());
    }

    @Test
    void testDeleteByImagePath_MultipleMedias_FindsCorrectOne() {
        // Given - Multiple medias, find specific one by path
        Media media2 = new Media();
        media2.setId("media-456");
        media2.setImagePath("different-path.jpg");
        media2.setProductId("product-456");

        List<Media> multipleMedias = List.of(testMedia, media2);
        when(mediaService.getAllMedias()).thenReturn(multipleMedias);
        when(mediaService.deleteMedia(testMedia)).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.deleteByImagePath(testMedia.getImagePath());

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mediaService, times(1)).deleteMedia(testMedia);
    }

    @Test
    void testConstructor() {
        // When
        MediaControllerImpl controller = new MediaControllerImpl(mediaService, s3Service);

        // Then
        assertThat(controller).isNotNull();
    }

    @Test
    void testConstants() {
        // Then
        assertThat(MediaControllerImpl.MESSAGE_KEY).isEqualTo("message");
        assertThat(MediaControllerImpl.MEDIA_KEY).isEqualTo("media");
    }

    @Test
    void testCreateMedia_VerifyImagePathSet() {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file);
        String uploadedPath = "s3://bucket/uploaded.jpg";

        when(s3Service.uploadFile(file)).thenReturn(uploadedPath);
        when(mediaService.saveMedia(any(Media.class))).thenAnswer(invocation -> {
            Media media = invocation.getArgument(0);
            assertThat(media.getImagePath()).isEqualTo(uploadedPath);
            return media;
        });

        // When
        mediaController.createMedia(files, "product-123");

        // Then
        verify(mediaService, times(1)).saveMedia(any(Media.class));
    }

    @Test
    void testGetMapResponseEntity_WithNullMedia() {
        // Given - Test private method via getMediaById
        when(mediaService.getMediaById("test")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaById("test");

        // Then - Verify private method getMapResponseEntity handles null
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetMapResponseEntity_WithValidMedia() {
        // Given - Test private method via getMediaById
        when(mediaService.getMediaById("test")).thenReturn(testMedia);

        // When
        ResponseEntity<Map<String, Object>> response = mediaController.getMediaById("test");

        // Then - Verify private method getMapResponseEntity handles valid media
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("media");
    }
}

