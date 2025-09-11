package com.example.mediaservice.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public String uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Le fichier ne peut pas être vide");
            }

            String originalFilename = file.getOriginalFilename() != null ?
                    file.getOriginalFilename() : "file";
            String fileName = System.currentTimeMillis() + "_" +
                    originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

            byte[] bytes = file.getBytes();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, fileName);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture fichier: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload S3: " + e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file, String customBucketName, String key) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Le fichier ne peut pas être vide");
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(customBucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    customBucketName, region, key);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture fichier: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload S3: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression S3: " + e.getMessage(), e);
        }
    }
}