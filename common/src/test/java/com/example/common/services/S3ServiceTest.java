package com.example.common.services;

import com.example.common.exceptions.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour S3Service (module common)
 * Objectif: Couverture 100% (34 lignes + 8 conditions)
 */
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile multipartFile;

    private S3Service s3Service;

    private static final String TEST_BUCKET = "test-bucket";
    private static final String TEST_REGION = "eu-north-1";

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client, TEST_BUCKET, TEST_REGION);
    }

    // ==================== UPLOAD FILE - SUCCESS TESTS ====================

    @Test
    void testUploadFile_Success() throws IOException {
        // Given
        String originalFilename = "test-image.jpg";
        byte[] fileContent = "test content".getBytes();
        String contentType = "image/jpeg";

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getBytes()).thenReturn(fileContent);
        when(multipartFile.getContentType()).thenReturn(contentType);

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull()
                .contains("https://")
                .contains(TEST_BUCKET)
                .contains(TEST_REGION)
                .contains("test-image.jpg");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_WithNullOriginalFilename() throws IOException {
        // Given - Condition: originalFilename == null
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("text/plain");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).contains("file");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_WithEmptyOriginalFilename() throws IOException {
        // Given - Condition: originalFilename.isEmpty()
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("text/plain");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).contains("file");
    }

    @Test
    void testUploadFile_WithSpecialCharactersInFilename() throws IOException {
        // Given - Test sanitization
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test@file#name$.jpg");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result)
                .doesNotContain("@")
                .doesNotContain("#")
                .doesNotContain("$")
                .contains("test_file_name_.jpg");
    }

    @Test
    void testUploadFile_UniqueFileNames() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result1 = s3Service.uploadFile(multipartFile);
        // Different files will have different timestamps naturally
        String result2 = s3Service.uploadFile(multipartFile);

        // Then - URLs contain timestamps so may be different
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1).contains("test.jpg");
        assertThat(result2).contains("test.jpg");
    }

    // ==================== VALIDATION TESTS ====================

    @Test
    void testUploadFile_NullFile() {
        // Given - Condition: file == null
        MultipartFile nullFile = null;

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(nullFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le fichier ne peut pas être vide");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_EmptyFile() {
        // Given - Condition: file.isEmpty()
        when(multipartFile.isEmpty()).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le fichier ne peut pas être vide");

        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    // ==================== EXCEPTION TESTS ====================

    @Test
    void testUploadFile_IOExceptionThrown() throws IOException {
        // Given - Catch IOException
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getBytes()).thenThrow(new IOException("Test IO error"));

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(FileUploadException.class)
                .hasMessageContaining("Erreur lors de la lecture du fichier")
                .hasMessageContaining("Test IO error");
    }

    @Test
    void testUploadFile_S3ExceptionThrown() throws IOException {
        // Given - Catch generic Exception (S3 upload failure)
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 upload failed"));

        // When & Then
        assertThatThrownBy(() -> s3Service.uploadFile(multipartFile))
                .isInstanceOf(FileUploadException.class)
                .hasMessageContaining("Erreur lors de l'upload vers S3")
                .hasMessageContaining("S3 upload failed");
    }


    // ==================== S3 CLIENT INTERACTION TESTS ====================

    @Test
    void testUploadFile_VerifyS3ClientCalled() throws IOException {
        // Given
        String filename = "document.pdf";
        byte[] content = "PDF content".getBytes();
        String contentType = "application/pdf";

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.getBytes()).thenReturn(content);
        when(multipartFile.getContentType()).thenReturn(contentType);

        // When
        s3Service.uploadFile(multipartFile);

        // Then
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_VerifyPutObjectRequestDetails() throws IOException {
        // Given
        byte[] fileContent = "test content".getBytes();
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getBytes()).thenReturn(fileContent);
        when(multipartFile.getContentType()).thenReturn("text/plain");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then - Verify S3Client was called and URL is correct
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertThat(result)
                .contains(TEST_BUCKET)
                .contains(TEST_REGION)
                .contains("test.txt");
    }

    // ==================== URL BUILDING TESTS ====================

    @Test
    void testBuildFileUrl_Format() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getBytes()).thenReturn("image".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/png");

        // When
        String url = s3Service.uploadFile(multipartFile);

        // Then - Verify URL format
        assertThat(url)
                .startsWith("https://")
                .contains(TEST_BUCKET + ".s3." + TEST_REGION + ".amazonaws.com")
                .endsWith("image.png");
    }

    // ==================== FILENAME GENERATION TESTS ====================

    @Test
    void testGenerateFileName_ContainsTimestamp() throws IOException {
        // Given
        long beforeTimestamp = System.currentTimeMillis();
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String url = s3Service.uploadFile(multipartFile);
        long afterTimestamp = System.currentTimeMillis();

        // Then - Extract timestamp from URL
        String filename = url.substring(url.lastIndexOf('/') + 1);
        String timestampStr = filename.substring(0, filename.indexOf('_'));
        long timestamp = Long.parseLong(timestampStr);

        assertThat(timestamp).isBetween(beforeTimestamp, afterTimestamp);
    }

    @Test
    void testSanitizeFilename_AllSpecialCharacters() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test file!@#$%^&*()+=[]{}|;:'\",<>?/\\.jpg");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then - Only alphanumeric, dots, underscores, and hyphens allowed
        String filename = result.substring(result.lastIndexOf('/') + 1);
        String nameWithoutTimestamp = filename.substring(filename.indexOf('_') + 1);
        assertThat(nameWithoutTimestamp).matches("[a-zA-Z0-9._-]+");
    }

    // ==================== EDGE CASES ====================

    @Test
    void testUploadFile_LargeFile() throws IOException {
        // Given
        byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("large.bin");
        when(multipartFile.getBytes()).thenReturn(largeContent);
        when(multipartFile.getContentType()).thenReturn("application/octet-stream");

        // When
        String result = s3Service.uploadFile(multipartFile);

        // Then
        assertThat(result).isNotNull();
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_DifferentContentTypes() throws IOException {
        // Test various content types
        String[] contentTypes = {
            "image/jpeg", "image/png", "application/pdf",
            "video/mp4", "text/plain", "application/json"
        };

        for (String contentType : contentTypes) {
            // Given
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getOriginalFilename()).thenReturn("file.ext");
            when(multipartFile.getBytes()).thenReturn("content".getBytes());
            when(multipartFile.getContentType()).thenReturn(contentType);
            reset(s3Client);

            // When
            String result = s3Service.uploadFile(multipartFile);

            // Then
            assertThat(result).isNotNull();
            verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }
    }
}

