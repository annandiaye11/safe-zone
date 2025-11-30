package com.cgl.userservice.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Service S3 pour user-service.
 * Délègue à l'implémentation commune du module common.
 *
 * @deprecated Utilisez directement com.example.common.services.S3Service
 */
@Deprecated
@Service
public class S3Service extends com.example.common.services.S3Service {

    public S3Service(S3Client s3Client,
                     @Value("${aws.s3.bucket}") String bucketName,
                     @Value("${aws.region}") String region) {
        super(s3Client, bucketName, region);
    }
}

