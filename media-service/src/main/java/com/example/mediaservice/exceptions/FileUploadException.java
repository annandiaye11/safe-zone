package com.example.mediaservice.exceptions;

/**
 * Alias pour la compatibilité ascendante.
 *
 * @deprecated Utilisez directement com.example.common.exceptions.FileUploadException
 */
@Deprecated
public class FileUploadException extends com.example.common.exceptions.FileUploadException {

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
