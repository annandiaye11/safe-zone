package com.example.common.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FileUploadExceptionTest {

    @Test
    void testFileUploadException_WithMessageAndCause() {
        // Given
        String message = "Erreur lors de l'upload du fichier";
        Throwable cause = new IOException("Network error");

        // When
        FileUploadException exception = new FileUploadException(message, cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testFileUploadException_WithNullCause() {
        // Given
        String message = "Erreur lors de l'upload";
        Throwable cause = null;

        // When
        FileUploadException exception = new FileUploadException(message, cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testFileUploadException_IsRuntimeException() {
        // Given
        FileUploadException exception = new FileUploadException("Test", new Exception("test"));

        // Then - Verify it's an unchecked exception
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testFileUploadException_CanBeThrown() {
        // Given
        String message = "Upload failed";
        IOException cause = new IOException("Disk full");

        // When & Then
        try {
            throw new FileUploadException(message, cause);
        } catch (FileUploadException e) {
            assertThat(e.getMessage()).isEqualTo(message);
            assertThat(e.getCause()).isEqualTo(cause);
            assertThat(e.getCause()).isInstanceOf(IOException.class);
        }
    }

    @Test
    void testFileUploadException_StackTrace() {
        // Given
        FileUploadException exception = new FileUploadException(
                "Error reading file",
                new IOException("File not found")
        );

        // When
        StackTraceElement[] stackTrace = exception.getStackTrace();

        // Then
        assertThat(stackTrace).isNotNull().hasSizeGreaterThan(0);
    }

    @Test
    void testFileUploadException_ChainedExceptions() {
        // Given
        IOException rootCause = new IOException("Root cause");
        RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
        FileUploadException exception = new FileUploadException("Top level", intermediateCause);

        // When & Then
        assertThat(exception.getCause()).isEqualTo(intermediateCause);
        assertThat(exception.getCause().getCause()).isEqualTo(rootCause);
    }

    @Test
    void testFileUploadException_MessageFormatting() {
        // Given
        String detailedMessage = "Erreur lors de la lecture du fichier: test.jpg";
        Throwable cause = new IOException("Permission denied");

        // When
        FileUploadException exception = new FileUploadException(detailedMessage, cause);

        // Then
        assertThat(exception.getMessage()).contains("test.jpg");
        assertThat(exception.getMessage()).contains("Erreur lors de la lecture du fichier");
        assertThat(exception.getCause().getMessage()).isEqualTo("Permission denied");
    }
}

