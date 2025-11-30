package com.example.common.exceptions;

/**
 * Exception levée lors d'une erreur d'upload de fichier vers S3.
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}

