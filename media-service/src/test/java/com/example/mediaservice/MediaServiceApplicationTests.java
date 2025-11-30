package com.example.mediaservice;

import com.example.mediaservice.data.entities.Media;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import static org.assertj.core.api.Assertions.assertThat;

class MediaServiceApplicationTests {

    @Test
    void testMainMethod() {
        // When & Then - Verify main method doesn't throw exception
        // We can't fully test Spring Boot main without starting the app,
        // but we can verify the class and method exist
        assertThat(MediaServiceApplication.class).isNotNull();
        assertThat(MediaServiceApplication.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    void testApplicationHasSpringBootApplicationAnnotation() {
        // Then - Verify @SpringBootApplication annotation is present
        assertThat(MediaServiceApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableMongoAuditingAnnotation() {
        // Then - Verify @EnableMongoAuditing annotation is present
        assertThat(MediaServiceApplication.class.isAnnotationPresent(EnableMongoAuditing.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableDiscoveryClientAnnotation() {
        // Then - Verify @EnableDiscoveryClient annotation is present
        assertThat(MediaServiceApplication.class.isAnnotationPresent(EnableDiscoveryClient.class)).isTrue();
    }

    @Test
    void testApplicationClassExists() {
        // Then - Verify the main application class exists and can be instantiated
        assertThat(MediaServiceApplication.class).isNotNull();
        assertThat(MediaServiceApplication.class.getSimpleName()).isEqualTo("MediaServiceApplication");
    }

    @Test
    void testApplicationPackage() {
        // Then - Verify the package
        assertThat(MediaServiceApplication.class.getPackage().getName()).isEqualTo("com.example.mediaservice");
    }

    @Test
    void testMediaEntityCreation() {
        // Given & When
        Media media = Media.builder()
                .id("media-1")
                .imagePath("/uploads/images/product-photo.jpg")
                .productId("product-123")
                .build();

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getId()).isEqualTo("media-1");
        assertThat(media.getImagePath()).isEqualTo("/uploads/images/product-photo.jpg");
        assertThat(media.getProductId()).isEqualTo("product-123");
    }

    @Test
    void testMediaEntityWithMinimalData() {
        // Given & When
        Media media = Media.builder()
                .imagePath("/minimal/image.png")
                .build();

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getImagePath()).isEqualTo("/minimal/image.png");
        assertThat(media.getId()).isNull();
        assertThat(media.getProductId()).isNull();
    }

    @Test
    void testMediaImagePathUpdate() {
        // Given
        Media media = Media.builder()
                .id("media-2")
                .imagePath("/old/path/image.jpg")
                .productId("product-456")
                .build();

        // When
        media.setImagePath("/new/path/updated-image.jpg");

        // Then
        assertThat(media.getImagePath()).isEqualTo("/new/path/updated-image.jpg");
        assertThat(media.getId()).isEqualTo("media-2");
    }

    @Test
    void testMediaProductIdUpdate() {
        // Given
        Media media = Media.builder()
                .id("media-3")
                .imagePath("/images/photo.jpg")
                .productId("old-product-789")
                .build();

        // When
        media.setProductId("new-product-999");

        // Then
        assertThat(media.getProductId()).isEqualTo("new-product-999");
    }

    @Test
    void testMediaGettersAndSetters() {
        // Given
        Media media = new Media();

        // When
        media.setId("media-4");
        media.setImagePath("/test/image.png");
        media.setProductId("test-product-100");

        // Then
        assertThat(media.getId()).isEqualTo("media-4");
        assertThat(media.getImagePath()).isEqualTo("/test/image.png");
        assertThat(media.getProductId()).isEqualTo("test-product-100");
    }

    @Test
    void testMediaAllArgsConstructor() {
        // Given & When
        Media media = new Media("media-5", "/constructor/image.jpg", "product-200");

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getId()).isEqualTo("media-5");
        assertThat(media.getImagePath()).isEqualTo("/constructor/image.jpg");
        assertThat(media.getProductId()).isEqualTo("product-200");
    }

    @Test
    void testMediaNoArgsConstructor() {
        // Given & When
        Media media = new Media();

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getId()).isNull();
        assertThat(media.getImagePath()).isNull();
        assertThat(media.getProductId()).isNull();
    }

    @Test
    void testMediaImagePathWithDifferentExtensions() {
        // Test avec .jpg
        Media jpgMedia = Media.builder()
                .imagePath("/images/photo.jpg")
                .build();
        assertThat(jpgMedia.getImagePath()).endsWith(".jpg");

        // Test avec .png
        Media pngMedia = Media.builder()
                .imagePath("/images/screenshot.png")
                .build();
        assertThat(pngMedia.getImagePath()).endsWith(".png");

        // Test avec .webp
        Media webpMedia = Media.builder()
                .imagePath("/images/modern.webp")
                .build();
        assertThat(webpMedia.getImagePath()).endsWith(".webp");
    }

    @Test
    void testMediaForMultipleProductImages() {
        // Given - Un produit peut avoir plusieurs images
        Media mainImage = Media.builder()
                .id("media-main-1")
                .imagePath("/products/product-123/main.jpg")
                .productId("product-123")
                .build();

        Media thumbnail = Media.builder()
                .id("media-thumb-1")
                .imagePath("/products/product-123/thumbnail.jpg")
                .productId("product-123")
                .build();

        Media detailImage = Media.builder()
                .id("media-detail-1")
                .imagePath("/products/product-123/detail.jpg")
                .productId("product-123")
                .build();

        // Then
        assertThat(mainImage.getProductId()).isEqualTo("product-123");
        assertThat(thumbnail.getProductId()).isEqualTo("product-123");
        assertThat(detailImage.getProductId()).isEqualTo("product-123");

        assertThat(mainImage.getImagePath()).contains("main");
        assertThat(thumbnail.getImagePath()).contains("thumbnail");
        assertThat(detailImage.getImagePath()).contains("detail");
    }

    @Test
    void testMediaIdGeneration() {
        // Given
        Media media1 = Media.builder()
                .imagePath("/images/test1.jpg")
                .productId("product-1")
                .build();

        Media media2 = Media.builder()
                .imagePath("/images/test2.jpg")
                .productId("product-2")
                .build();

        // When - En situation réelle, MongoDB générerait les IDs
        media1.setId("generated-id-1");
        media2.setId("generated-id-2");

        // Then
        assertThat(media1.getId()).isNotEqualTo(media2.getId());
        assertThat(media1.getId()).isEqualTo("generated-id-1");
        assertThat(media2.getId()).isEqualTo("generated-id-2");
    }

}
