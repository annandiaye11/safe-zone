package com.example.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

class S3ConfigTest {

    @Test
    void testS3ClientCreation() {
        // Given
        S3Config s3Config = new S3Config();
        ReflectionTestUtils.setField(s3Config, "region", "eu-north-1");
        ReflectionTestUtils.setField(s3Config, "accessKey", "test-access-key");
        ReflectionTestUtils.setField(s3Config, "secretKey", "test-secret-key");

        // When
        S3Client s3Client = s3Config.s3Client();

        // Then
        assertThat(s3Client).isInstanceOf(S3Client.class);
    }

    @Test
    void testS3ConfigFieldsAreSet() {
        // Given
        S3Config s3Config = new S3Config();
        ReflectionTestUtils.setField(s3Config, "region", "us-east-1");
        ReflectionTestUtils.setField(s3Config, "accessKey", "AKIAIOSFODNN7EXAMPLE");
        ReflectionTestUtils.setField(s3Config, "secretKey", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");

        // When
        Object region = ReflectionTestUtils.getField(s3Config, "region");
        Object accessKey = ReflectionTestUtils.getField(s3Config, "accessKey");
        Object secretKey = ReflectionTestUtils.getField(s3Config, "secretKey");

        // Then
        assertThat(region).isEqualTo("us-east-1");
        assertThat(accessKey).isEqualTo("AKIAIOSFODNN7EXAMPLE");
        assertThat(secretKey).isEqualTo("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
    }
}

