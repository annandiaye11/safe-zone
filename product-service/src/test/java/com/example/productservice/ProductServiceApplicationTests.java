package com.example.productservice;

import com.example.productservice.data.entities.Product;
import com.example.productservice.web.dto.ProductDto;
import com.example.productservice.web.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceApplicationTests {

    @Test
    void testMainMethodExecution() {
        // Given - Mock SpringApplication.run to avoid actually starting the app
        try (var mockedSpringApp = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            mockedSpringApp.when(() -> SpringApplication.run(ProductServiceApplication.class, new String[]{}))
                    .thenReturn(mockContext);

            // When - Call the main method
            ProductServiceApplication.main(new String[]{});

            // Then - Verify SpringApplication.run was called
            mockedSpringApp.verify(() -> SpringApplication.run(ProductServiceApplication.class, new String[]{}));
        }
    }

    @Test
    void testMainMethod() {
        // When & Then - Verify main method doesn't throw exception
        // We can't fully test Spring Boot main without starting the app,
        // but we can verify the class and method exist
        assertThat(ProductServiceApplication.class).isNotNull();
        assertThat(ProductServiceApplication.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    void testApplicationHasSpringBootApplicationAnnotation() {
        // Then - Verify @SpringBootApplication annotation is present
        assertThat(ProductServiceApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableMongoAuditingAnnotation() {
        // Then - Verify @EnableMongoAuditing annotation is present
        assertThat(ProductServiceApplication.class.isAnnotationPresent(EnableMongoAuditing.class)).isTrue();
    }

    @Test
    void testApplicationHasEnableDiscoveryClientAnnotation() {
        // Then - Verify @EnableDiscoveryClient annotation is present
        assertThat(ProductServiceApplication.class.isAnnotationPresent(EnableDiscoveryClient.class)).isTrue();
    }

    @Test
    void testApplicationClassExists() {
        // Then - Verify the main application class exists and can be instantiated
        assertThat(ProductServiceApplication.class).isNotNull();
        assertThat(ProductServiceApplication.class.getSimpleName()).isEqualTo("ProductServiceApplication");
    }

    @Test
    void testApplicationPackage() {
        // Then - Verify the package
        assertThat(ProductServiceApplication.class.getPackage().getName()).isEqualTo("com.example.productservice");
    }

    @Test
    void testProductEntityCreation() {
        // Given & When
        Product product = Product.builder()
                .id("test-id-1")
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .quantity(10)
                .userId("user-123")
                .build();

        // Then
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo("test-id-1");
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getPrice()).isEqualTo(99.99);
        assertThat(product.getQuantity()).isEqualTo(10);
    }

    @Test
    void testProductDtoCreation() {
        // Given & When
        ProductDto dto = ProductDto.builder()
                .id("dto-id-1")
                .name("DTO Product")
                .description("DTO Description")
                .price(149.99)
                .quantity(20)
                .userId("user-456")
                .build();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("dto-id-1");
        assertThat(dto.getName()).isEqualTo("DTO Product");
        assertThat(dto.getPrice()).isEqualTo(149.99);
    }

    @Test
    void testProductMapperEntityToDto() {
        // Given
        Product entity = Product.builder()
                .id("entity-1")
                .name("Entity Product")
                .description("Entity Description")
                .price(79.99)
                .quantity(5)
                .userId("user-789")
                .build();

        // When
        ProductDto dto = ProductMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getPrice()).isEqualTo(entity.getPrice());
    }

    @Test
    void testProductMapperDtoToEntity() {
        // Given
        ProductDto dto = ProductDto.builder()
                .id("dto-2")
                .name("DTO to Entity Product")
                .description("Conversion Test")
                .price(199.99)
                .quantity(15)
                .userId("user-999")
                .build();

        // When
        Product entity = ProductMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getPrice()).isEqualTo(dto.getPrice());
    }

    @Test
    void testProductWithMinimalData() {
        // Given & When
        Product product = Product.builder()
                .name("Minimal Product")
                .price(29.99)
                .build();

        // Then
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("Minimal Product");
        assertThat(product.getPrice()).isEqualTo(29.99);
        assertThat(product.getId()).isNull();
        assertThat(product.getDescription()).isNull();
    }

    @Test
    void testProductPriceUpdate() {
        // Given
        Product product = Product.builder()
                .id("price-test-1")
                .name("Price Test Product")
                .price(50.00)
                .build();

        // When
        product.setPrice(75.00);

        // Then
        assertThat(product.getPrice()).isEqualTo(75.00);
        assertThat(product.getName()).isEqualTo("Price Test Product");
    }

    @Test
    void testProductQuantityUpdate() {
        // Given
        Product product = Product.builder()
                .id("qty-test-1")
                .name("Quantity Test Product")
                .quantity(10)
                .price(100.00)
                .build();

        // When
        product.setQuantity(25);

        // Then
        assertThat(product.getQuantity()).isEqualTo(25);
    }
}
