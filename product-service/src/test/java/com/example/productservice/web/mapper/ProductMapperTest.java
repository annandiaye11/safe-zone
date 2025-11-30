package com.example.productservice.web.mapper;

import com.example.productservice.data.entities.Product;
import com.example.productservice.web.dto.ProductDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ProductMapperTest {

    @Test
    void testToEntity_Success() {
        // Given
        ProductDto dto = ProductDto.builder()
                .id("1")
                .name("Test Product")
                .description("Test Description")
                .price(100.0)
                .quantity(10)
                .userId("user123")
                .build();

        // When
        Product entity = ProductMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("1");
        assertThat(entity.getName()).isEqualTo("Test Product");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getPrice()).isEqualTo(100.0);
        assertThat(entity.getQuantity()).isEqualTo(10);
        assertThat(entity.getUserId()).isEqualTo("user123");
    }

    @Test
    void testToDto_Success() {
        // Given
        Product entity = Product.builder()
                .id("2")
                .name("Another Product")
                .description("Another Description")
                .price(200.0)
                .quantity(20)
                .userId("user456")
                .build();

        // When
        ProductDto dto = ProductMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("2");
        assertThat(dto.getName()).isEqualTo("Another Product");
        assertThat(dto.getDescription()).isEqualTo("Another Description");
        assertThat(dto.getPrice()).isEqualTo(200.0);
        assertThat(dto.getQuantity()).isEqualTo(20);
        assertThat(dto.getUserId()).isEqualTo("user456");
    }

    @Test
    void testToEntity_WithNullValues() {
        // Given
        ProductDto dto = ProductDto.builder()
                .name("Minimal Product")
                .price(50.0)
                .build();

        // When
        Product entity = ProductMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Minimal Product");
        assertThat(entity.getPrice()).isEqualTo(50.0);
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getUserId()).isNull();
    }

    @Test
    void testToDto_WithNullValues() {
        // Given
        Product entity = Product.builder()
                .name("Minimal Product")
                .price(50.0)
                .build();

        // When
        ProductDto dto = ProductMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Minimal Product");
        assertThat(dto.getPrice()).isEqualTo(50.0);
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getUserId()).isNull();
    }

    @Test
    void testBidirectionalMapping() {
        // Given
        ProductDto originalDto = ProductDto.builder()
                .id("original-id")
                .name("Original Product")
                .description("Original Description")
                .price(99.99)
                .quantity(5)
                .userId("user789")
                .build();

        // When - Convert DTO to Entity and back to DTO
        Product entity = ProductMapper.toEntity(originalDto);
        ProductDto resultDto = ProductMapper.toDto(entity);

        // Then - Verify bidirectional mapping preserves all data
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(originalDto.getId());
        assertThat(resultDto.getName()).isEqualTo(originalDto.getName());
        assertThat(resultDto.getDescription()).isEqualTo(originalDto.getDescription());
        assertThat(resultDto.getPrice()).isEqualTo(originalDto.getPrice());
        assertThat(resultDto.getQuantity()).isEqualTo(originalDto.getQuantity());
        assertThat(resultDto.getUserId()).isEqualTo(originalDto.getUserId());
    }

    @Test
    void testConstructor_ThrowsException() throws Exception {
        // When & Then - Verify that the private constructor throws UnsupportedOperationException
        assertThat(ProductMapper.class.getDeclaredConstructors()).hasSize(1);
        assertThat(ProductMapper.class.getDeclaredConstructors()[0].canAccess(null)).isFalse();

        // Verify constructor is private and throws exception when invoked via reflection
        var constructor = ProductMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        try {
            constructor.newInstance();
            // Should not reach here
            fail("Constructor should throw UnsupportedOperationException");
        } catch (Exception e) {
            assertThat(e.getCause()).isInstanceOf(UnsupportedOperationException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("This is a utility class and cannot be instantiated");
        }
    }
}

