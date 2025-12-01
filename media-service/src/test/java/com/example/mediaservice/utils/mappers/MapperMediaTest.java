package com.example.mediaservice.utils.mappers;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.web.dto.requests.MediaDtoAll;
import com.example.mediaservice.web.dto.responses.MediaResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class MapperMediaTest {

    // ==================== CONSTRUCTOR TEST ====================

    @Test
    void testConstructor_ThrowsException() throws Exception {
        // When & Then - Verify that the private constructor throws UnsupportedOperationException
        assertThat(MapperMedia.class.getDeclaredConstructors()).hasSize(1);
        assertThat(MapperMedia.class.getDeclaredConstructors()[0].canAccess(null)).isFalse();

        // Verify constructor is private and throws exception when invoked via reflection
        var constructor = MapperMedia.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            constructor.newInstance();
            fail("Constructor should throw UnsupportedOperationException");
        } catch (Exception e) {
            assertThat(e.getCause()).isInstanceOf(UnsupportedOperationException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("This is a utility class and cannot be instantiated");
        }
    }

    // ==================== TO ENTITY TESTS ====================

    @Test
    void testToEntity_Success() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("product-123");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getProductId()).isEqualTo("product-123");
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
    void testToEntity_WithEmptyProductId() {
        // Given
        MediaDtoAll mediaDtoAll = new MediaDtoAll();
        mediaDtoAll.setProductId("");

        // When
        Media media = MapperMedia.toEntity(mediaDtoAll);

        // Then
        assertThat(media).isNotNull();
        assertThat(media.getProductId()).isEmpty();
    }

    // ==================== TO DTO (MediaResponse) TESTS ====================

    @Test
    void testToDto_Success() {
        // Given
        Media media = new Media();
        media.setId("media-123");
        media.setImagePath("/images/product.jpg");
        media.setProductId("product-456");

        // When
        MediaResponse response = MapperMedia.toDto(media);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("media-123");
        assertThat(response.getImagePath()).isEqualTo("/images/product.jpg");
        assertThat(response.getProductId()).isEqualTo("product-456");
    }

    @Test
    void testToDto_WithNullValues() {
        // Given
        Media media = new Media();
        media.setId(null);
        media.setImagePath(null);
        media.setProductId(null);

        // When
        MediaResponse response = MapperMedia.toDto(media);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getImagePath()).isNull();
        assertThat(response.getProductId()).isNull();
    }

    @Test
    void testToDto_WithEmptyStrings() {
        // Given
        Media media = new Media();
        media.setId("");
        media.setImagePath("");
        media.setProductId("");

        // When
        MediaResponse response = MapperMedia.toDto(media);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEmpty();
        assertThat(response.getImagePath()).isEmpty();
        assertThat(response.getProductId()).isEmpty();
    }

    // ==================== BIDIRECTIONAL MAPPING TESTS ====================

    @Test
    void testBidirectionalMapping_ToEntityAndToDto() {
        // Given
        MediaDtoAll originalDto = new MediaDtoAll();
        originalDto.setProductId("product-789");

        // When
        Media entity = MapperMedia.toEntity(originalDto);
        entity.setId("generated-id");
        entity.setImagePath("/uploaded/image.png");
        MediaResponse resultDto = MapperMedia.toDto(entity);

        // Then
        assertThat(resultDto.getProductId()).isEqualTo(originalDto.getProductId());
        assertThat(resultDto.getId()).isEqualTo("generated-id");
        assertThat(resultDto.getImagePath()).isEqualTo("/uploaded/image.png");
    }

    // ==================== EDGE CASES ====================

    @Test
    void testMapperMediaClassExists() {
        // Then - Verify class exists and is not abstract
        assertThat(MapperMedia.class).isNotNull();
        assertThat(java.lang.reflect.Modifier.isFinal(MapperMedia.class.getModifiers())).isFalse();
    }

    @Test
    void testToEntity_MultipleCalls() {
        // Given
        MediaDtoAll dto1 = new MediaDtoAll();
        dto1.setProductId("product-1");

        MediaDtoAll dto2 = new MediaDtoAll();
        dto2.setProductId("product-2");

        // When
        Media media1 = MapperMedia.toEntity(dto1);
        Media media2 = MapperMedia.toEntity(dto2);

        // Then - Each call creates a new instance
        assertThat(media1).isNotSameAs(media2);
        assertThat(media1.getProductId()).isEqualTo("product-1");
        assertThat(media2.getProductId()).isEqualTo("product-2");
    }

    @Test
    void testToDto_MultipleCalls() {
        // Given
        Media entity1 = new Media();
        entity1.setId("id-1");
        entity1.setImagePath("/path1");
        entity1.setProductId("product-1");

        Media entity2 = new Media();
        entity2.setId("id-2");
        entity2.setImagePath("/path2");
        entity2.setProductId("product-2");

        // When
        MediaResponse dto1 = MapperMedia.toDto(entity1);
        MediaResponse dto2 = MapperMedia.toDto(entity2);

        // Then - Each call creates a new instance
        assertThat(dto1).isNotSameAs(dto2);
        assertThat(dto1.getId()).isEqualTo("id-1");
        assertThat(dto2.getId()).isEqualTo("id-2");
    }
}

