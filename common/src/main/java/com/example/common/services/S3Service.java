package com.example.common.services;

import com.example.common.exceptions.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    // Constructor injection (recommandé par Spring)
    public S3Service(S3Client s3Client,
                     @Value("${aws.s3.bucket}") String bucketName,
                     @Value("${aws.region}") String region) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
    }

    /**
     * Upload un fichier vers S3 et retourne l'URL publique du fichier.
     *
     * @param file le fichier à uploader
     * @return l'URL publique du fichier uploadé
     * @throws FileUploadException en cas d'erreur lors de l'upload
     * @throws IllegalArgumentException si le fichier est null ou vide
     */
    public String uploadFile(MultipartFile file) {
        try {
            validateFile(file);

            String fileName = generateFileName(file);
            byte[] bytes = file.getBytes();

            uploadToS3(fileName, bytes, file.getContentType());

            return buildFileUrl(fileName);

        } catch (IOException e) {
            throw new FileUploadException("Erreur lors de la lecture du fichier: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new FileUploadException("Erreur lors de l'upload vers S3: " + e.getMessage(), e);
        }
    }

    /**
     * Valide que le fichier n'est pas null ou vide.
     *
     * @param file le fichier à valider
     * @throws IllegalArgumentException si le fichier est null ou vide
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier ne peut pas être vide");
        }
    }

    /**
     * Génère un nom de fichier unique basé sur le timestamp et le nom original.
     *
     * @param file le fichier
     * @return le nom de fichier généré
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String baseName = (originalFilename != null && !originalFilename.isEmpty())
                ? originalFilename
                : "file";

        String sanitizedName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return System.currentTimeMillis() + "_" + sanitizedName;
    }

    /**
     * Upload les bytes du fichier vers S3.
     *
     * @param fileName le nom du fichier
     * @param bytes les bytes du fichier
     * @param contentType le type MIME du fichier
     */
    private void uploadToS3(String fileName, byte[] bytes, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }

    /**
     * Construit l'URL publique du fichier sur S3.
     *
     * @param fileName le nom du fichier
     * @return l'URL publique complète
     */
    private String buildFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, fileName);
    }
}

