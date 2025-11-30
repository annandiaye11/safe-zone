package com.example.mediaservice.utils.mappers;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.web.dto.requests.MediaDtoAll;
import com.example.mediaservice.web.dto.responses.MediaResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperMediaTest {

    @Test
    void testToEntity_ConvertsMediaDtoAllToMedia() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-123");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getProductId()).isEqualTo("product-123");
        assertThat(media.getId()).isNull(); // Not set by mapper
        assertThat(media.getImagePath()).isNull(); // Not set by mapper
    }

    @Test
    void testToEntity_WithDifferentProductId() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-456");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media.getProductId()).isEqualTo("product-456");
    }

    @Test
    void testToEntity_WithNullProductId() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId(null);

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getProductId()).isNull();
    }

    @Test
    void testToEntity_CreatesNewMediaInstance() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-789");

        // When
        Media media1 = MapperMedia.toEntity(mediaDtoAll);
        Media media2 = MapperMedia.toEntity(mediaDtoAll);

        // Then - Each call creates a new instance
        assertThat(media1).isNotSameAs(media2);
        assertThat(media1.getProductId()).isEqualTo(media2.getProductId());
    }

    @Test
    void testToDto_ConvertsMediaToMediaResponse() {
        // Given
        Media media = new Media();
        media.setId("media-123");
        media.setImagePath("/path/to/image.jpg");
        media.setProductId("product-123");

        // When
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then
        assertThat(mediaResponse).isNotNull();
        assertThat(mediaResponse.getId()).isEqualTo("media-123");
        assertThat(mediaResponse.getImagePath()).isEqualTo("/path/to/image.jpg");
        assertThat(mediaResponse.getProductId()).isEqualTo("product-123");
    }

    @Test
    void testToDto_WithDifferentValues() {
        // Given
        Media media = new Media();
        media.setId("media-456");
        media.setImagePath("/images/product.png");
        media.setProductId("product-789");

        // When
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then
        assertThat(mediaResponse.getId()).isEqualTo("media-456");
        assertThat(mediaResponse.getImagePath()).isEqualTo("/images/product.png");
        assertThat(mediaResponse.getProductId()).isEqualTo("product-789");
    }

    @Test
    void testToDto_WithNullValues() {
        // Given
        Media media = new Media();
        media.setId(null);
        media.setImagePath(null);
        media.setProductId(null);

        // When
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then
        assertThat(mediaResponse).isNotNull();
        assertThat(mediaResponse.getId()).isNull();
        assertThat(mediaResponse.getImagePath()).isNull();
        assertThat(mediaResponse.getProductId()).isNull();
    }

    @Test
    void testToDto_CreatesNewMediaResponseInstance() {
        // Given
        Media media = new Media();
        media.setId("media-123");
        media.setImagePath("/path/image.jpg");
        media.setProductId("product-123");

        // When
        MediaResponse response1 = MapperMedia.toDto(media);
        MediaResponse response2 = MapperMedia.toDto(media);

        // Then - Each call creates a new instance
        assertThat(response1).isNotSameAs(response2);
        assertThat(response1.getId()).isEqualTo(response2.getId());
        assertThat(response1.getImagePath()).isEqualTo(response2.getImagePath());
        assertThat(response1.getProductId()).isEqualTo(response2.getProductId());
    }

    @Test
    void testToEntity_OnlyMapsProductId() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-999");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then - Verify only productId is mapped
        assertThat(media.getProductId()).isEqualTo("product-999");
        // Other fields should be null (default values)
        assertThat(media.getId()).isNull();
        assertThat(media.getImagePath()).isNull();
    }

    @Test
    void testToDto_MapsAllMediaFields() {
        // Given
        Media media = new Media();
        media.setId("full-media-id");
        media.setImagePath("https://example.com/images/media.jpg");
        media.setProductId("full-product-id");

        // When
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then - Verify all fields are mapped
        assertThat(mediaResponse.getId()).isEqualTo("full-media-id");
        assertThat(mediaResponse.getImagePath()).isEqualTo("https://example.com/images/media.jpg");
        assertThat(mediaResponse.getProductId()).isEqualTo("full-product-id");
    }

    @Test
    void testToEntity_WithEmptyProductId() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media.getProductId()).isEmpty();
    }

    @Test
    void testToDto_WithEmptyStrings() {
        // Given
        Media media = new Media();
        media.setId("");
        media.setImagePath("");
        media.setProductId("");

        // When
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then
        assertThat(mediaResponse.getId()).isEmpty();
        assertThat(mediaResponse.getImagePath()).isEmpty();
        assertThat(mediaResponse.getProductId()).isEmpty();
    }

    @Test
    void testToEntity_WithLongProductId() {
        // Given
        String longProductId = "product-" + "a".repeat(100);
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId(longProductId);

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media.getProductId()).isEqualTo(longProductId);
        assertThat(media.getProductId().length()).isGreaterThan(100);
    }

    @Test
    void testToDto_WithLongImagePath() {
        // Given
        String longPath = "https://example.com/very/long/path/" + "segment/".repeat(20) + "image.jpg";
        Media media = new Media();
        media.setId("media-id");
        media.setImagePath(longPath);
        media.setProductId("product-id");

        // When
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then
        assertThat(mediaResponse.getImagePath()).isEqualTo(longPath);
        assertThat(mediaResponse.getImagePath().length()).isGreaterThan(50);
    }

    @Test
    void testRoundTrip_EntityToDtoPreservesData() {
        // Given - Create a Media entity
        Media originalMedia = new Media();
        originalMedia.setId("round-trip-id");
        originalMedia.setImagePath("/images/test.jpg");
        originalMedia.setProductId("round-trip-product");

        // When - Convert to DTO
        MediaResponse mediaResponse = MapperMedia.toDto(originalMedia);

        // Then - All data is preserved in the DTO
        assertThat(mediaResponse.getId()).isEqualTo(originalMedia.getId());
        assertThat(mediaResponse.getImagePath()).isEqualTo(originalMedia.getImagePath());
        assertThat(mediaResponse.getProductId()).isEqualTo(originalMedia.getProductId());
    }

    @Test
    void testToEntity_TypicalUsageScenario() {
        // Given - Typical usage when creating a new media from DTO
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-abc123");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then - Ready to be saved to database (id will be generated by DB)
        assertThat(media).isNotNull();
        assertThat(media.getProductId()).isEqualTo("product-abc123");
        assertThat(media.getId()).isNull(); // Will be generated by database
    }

    @Test
    void testToDto_TypicalUsageScenario() {
        // Given - Media entity retrieved from database
        Media media = new Media();
        media.setId("db-generated-id");
        media.setImagePath("https://s3.amazonaws.com/bucket/media/image.jpg");
        media.setProductId("product-xyz789");

        // When - Convert to DTO for API response
        MediaResponse mediaResponse = MapperMedia.toDto(media);

        // Then - DTO ready to be sent in API response
        assertThat(mediaResponse).isNotNull();
        assertThat(mediaResponse.getId()).isNotNull();
        assertThat(mediaResponse.getImagePath()).startsWith("https://");
        assertThat(mediaResponse.getProductId()).isNotNull();
    }
}

