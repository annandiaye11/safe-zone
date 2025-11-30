package com.cgl.userservice.services.impl;

import com.cgl.userservice.exception.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile multipartFile;

    private S3Service s3Service;

    private static final String TEST_BUCKET = "user-service-bucket";
    private static final String TEST_REGION = "eu-north-1";

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client);
        ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET);
        ReflectionTestUtils.setField(s3Service, "region", TEST_REGION);
    }

    @Test
    void testUploadFile_Success() throws IOException {
        // Given
        String originalFilename = "avatar.jpg";
        byte[] fileContent = "avatar content".getBytes();
        String contentType = "image/jpeg";

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getBytes()).thenReturn(fileContent);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any())).thenReturn(null);

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull().contains("https://").contains(TEST_BUCKET).contains(TEST_REGION).contains("avatar.jpg");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void testUploadFile_NullFile() {
        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas être vide");
    }

    @Test
    void testUploadFile_EmptyFile() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ne peut pas être vide");
    }

    @Test
    void testUploadFile_NullOriginalFilename() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull().contains("file");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void testUploadFile_EmptyOriginalFilename() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull().contains("file");
    }

    @Test
    void testUploadFile_SpecialCharactersInFilename() throws IOException {
        // Given
        String filenameWithSpecialChars = "user@avatar#2024$.jpg";
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(filenameWithSpecialChars);
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull().doesNotContain("@", "#", "$");
    }

    @Test
    void testUploadFile_IOExceptionThrown() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("avatar.jpg");
        when(multipartFile.getBytes()).thenThrow(new IOException("Cannot read file"));

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(FileUploadException.class)
                .hasMessageContaining("Erreur lors de la lecture du fichier");
    }

    @Test
    void testUploadFile_S3ExceptionThrown() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("avatar.jpg");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any()))
                .thenThrow(new RuntimeException("S3 connection failed"));

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(FileUploadException.class)
                .hasMessageContaining("Erreur lors de l'upload vers S3");
    }

    @Test
    void testFileUploadException_Constructor() {
        // Given
        String message = "Upload failed";
        Throwable cause = new IOException("Network error");

        // When
        FileUploadException exception = new FileUploadException(message, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testUploadFile_DifferentFileTypes() throws IOException {
        // Test PNG
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("profile.png");
        when(multipartFile.getBytes()).thenReturn("png content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/png");

        String pngResult = s3Service.uploadFile(multipartFile);
        assertThat(pngResult).contains("profile.png");

        // Test JPEG
        when(multipartFile.getOriginalFilename()).thenReturn("photo.jpeg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        String jpegResult = s3Service.uploadFile(multipartFile);
        assertThat(jpegResult).contains("photo.jpeg");
    }

    @Test
    void testUploadFile_LongFilename() throws IOException {
        // Given
        String longFilename = "this_is_a_very_long_filename_for_testing_purposes_with_many_characters.jpg";
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(longFilename);
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull().contains(longFilename);
    }
}

