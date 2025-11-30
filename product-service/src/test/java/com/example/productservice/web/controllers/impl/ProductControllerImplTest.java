package com.example.productservice.web.controllers.impl;

import com.example.productservice.data.entities.Product;
import com.example.productservice.service.ProductService;
import com.example.productservice.web.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerImplTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductControllerImpl productController;

    private Product testProduct;
    private List<Product> testProductList;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("product-123")
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .userId("user-123")
                .build();

        testProductList = new ArrayList<>();
        testProductList.add(testProduct);
    }

    // ==================== create() TESTS ====================

    @Test
    void testCreate_NewProduct_ReturnsCreated() {
        // Given - Product doesn't exist (Condition 1: getByName returns null)
        ProductDto newProductDto = ProductDto.builder()
                .name("New Product")
                .description("Description")
                .price(50.0)
                .userId("user-456")
                .build();

        Product savedProduct = Product.builder()
                .id("product-456")
                .name("New Product")
                .description("Description")
                .price(50.0)
                .userId("user-456")
                .build();

        when(productService.getByName("New Product")).thenReturn(null);
        when(productService.create(any(Product.class))).thenReturn(savedProduct);

        // When
        ResponseEntity<ProductDto> response = productController.create(newProductDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("product-456");
        assertThat(response.getBody().getName()).isEqualTo("New Product");

        verify(productService, times(1)).getByName("New Product");
        verify(productService, times(1)).create(any(Product.class));
    }

    @Test
    void testCreate_ProductAlreadyExists_ReturnsBadRequest() {
        // Given - Product exists (Condition 2: getByName returns existing product)
        ProductDto existingProductDto = ProductDto.builder()
                .name("Existing Product")
                .description("Description")
                .price(75.0)
                .build();

        when(productService.getByName("Existing Product")).thenReturn(testProduct);

        // When
        ResponseEntity<ProductDto> response = productController.create(existingProductDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();

        verify(productService, times(1)).getByName("Existing Product");
        verify(productService, never()).create(any());
    }

    // ==================== getAll() TESTS ====================

    @Test
    void testGetAll_WithProducts_ReturnsProductList() {
        // Given
        when(productService.getAllProducts()).thenReturn(testProductList);

        // When
        ResponseEntity<List<ProductDto>> response = productController.getAll();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Test Product");

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetAll_NoProducts_ReturnsEmptyList() {
        // Given - Empty list (Condition 3: products.isEmpty())
        when(productService.getAllProducts()).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<List<ProductDto>> response = productController.getAll();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetAll_MultipleProducts() {
        // Given - Multiple products
        Product product2 = Product.builder()
                .id("product-456")
                .name("Product 2")
                .description("Description 2")
                .price(150.0)
                .userId("user-456")
                .build();

        List<Product> multipleProducts = List.of(testProduct, product2);
        when(productService.getAllProducts()).thenReturn(multipleProducts);

        // When
        ResponseEntity<List<ProductDto>> response = productController.getAll();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    // ==================== getById() TESTS ====================

    @Test
    void testGetById_ProductExists_ReturnsProduct() {
        // Given
        when(productService.getById("product-123")).thenReturn(testProduct);

        // When
        ResponseEntity<ProductDto> response = productController.getById("product-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("product-123");
        assertThat(response.getBody().getName()).isEqualTo("Test Product");

        verify(productService, times(1)).getById("product-123");
    }

    @Test
    void testGetById_ProductNotFound_Returns404() {
        // Given - Product not found (Condition 4: product == null)
        when(productService.getById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<ProductDto> response = productController.getById("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(productService, times(1)).getById("non-existent");
    }

    // ==================== update() TESTS ====================

    @Test
    void testUpdate_ProductExists_UpdatesProduct() {
        // Given
        ProductDto updateDto = ProductDto.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(199.99)
                .userId("user-123")
                .build();

        when(productService.getById("product-123")).thenReturn(testProduct);
        when(productService.update(any(Product.class))).thenReturn(testProduct);

        // When
        ResponseEntity<ProductDto> response = productController.update("product-123", updateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("product-123");
        assertThat(response.getBody().getName()).isEqualTo("Updated Product");

        verify(productService, times(1)).getById("product-123");
        verify(productService, times(1)).update(any(Product.class));
    }

    @Test
    void testUpdate_ProductNotFound_Returns404() {
        // Given - Product not found (Condition 5: existingProduct == null)
        ProductDto updateDto = ProductDto.builder()
                .name("Updated Product")
                .build();

        when(productService.getById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<ProductDto> response = productController.update("non-existent", updateDto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(productService, times(1)).getById("non-existent");
        verify(productService, never()).update(any());
    }

    // ==================== delete() TESTS ====================

    @Test
    void testDelete_ProductExists_DeletesProduct() {
        // Given
        when(productService.getById("product-123")).thenReturn(testProduct);
        doNothing().when(productService).delete("product-123");

        // When
        ResponseEntity<Void> response = productController.delete("product-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(productService, times(1)).getById("product-123");
        verify(productService, times(1)).delete("product-123");
    }

    @Test
    void testDelete_ProductNotFound_Returns404() {
        // Given - Product not found (Condition 6: existingProduct == null)
        when(productService.getById("non-existent")).thenReturn(null);

        // When
        ResponseEntity<Void> response = productController.delete("non-existent");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(productService, times(1)).getById("non-existent");
        verify(productService, never()).delete(anyString());
    }

    // ==================== getByUserId() TESTS ====================

    @Test
    void testGetByUserId_ProductsExist_ReturnsProductList() {
        // Given
        when(productService.getByUserId("user-123")).thenReturn(testProductList);

        // When
        ResponseEntity<List<ProductDto>> response = productController.getByUserId("user-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getUserId()).isEqualTo("user-123");

        verify(productService, times(1)).getByUserId("user-123");
    }

    @Test
    void testGetByUserId_ProductsNull_ReturnsEmptyList() {
        // Given - Products null (Condition 7: products == null)
        when(productService.getByUserId("user-456")).thenReturn(null);

        // When
        ResponseEntity<List<ProductDto>> response = productController.getByUserId("user-456");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(productService, times(1)).getByUserId("user-456");
    }

    @Test
    void testGetByUserId_ProductsEmpty_ReturnsEmptyList() {
        // Given - Empty list (Condition 8: products.isEmpty())
        when(productService.getByUserId("user-789")).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<List<ProductDto>> response = productController.getByUserId("user-789");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        verify(productService, times(1)).getByUserId("user-789");
    }

    @Test
    void testGetByUserId_MultipleProducts() {
        // Given - Multiple products for same user
        Product product2 = Product.builder()
                .id("product-789")
                .name("Product 2")
                .description("Description 2")
                .price(200.0)
                .userId("user-123")
                .build();

        List<Product> userProducts = List.of(testProduct, product2);
        when(productService.getByUserId("user-123")).thenReturn(userProducts);

        // When
        ResponseEntity<List<ProductDto>> response = productController.getByUserId("user-123");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    // ==================== EDGE CASES & INTEGRATION TESTS ====================

    @Test
    void testCreate_VerifyDtoIdIsSet() {
        // Given
        ProductDto newProductDto = ProductDto.builder()
                .name("New Product")
                .description("Description")
                .price(50.0)
                .build();

        Product savedProduct = Product.builder()
                .id("generated-id-123")
                .name("New Product")
                .build();

        when(productService.getByName("New Product")).thenReturn(null);
        when(productService.create(any(Product.class))).thenReturn(savedProduct);

        // When
        ResponseEntity<ProductDto> response = productController.create(newProductDto);

        // Then - Verify that the DTO id is set from saved product
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("generated-id-123");
    }

    @Test
    void testUpdate_VerifyDtoIdIsSet() {
        // Given
        ProductDto updateDto = ProductDto.builder()
                .name("Updated")
                .build();
        // No id initially

        when(productService.getById("product-123")).thenReturn(testProduct);
        when(productService.update(any(Product.class))).thenReturn(testProduct);

        // When
        ResponseEntity<ProductDto> response = productController.update("product-123", updateDto);

        // Then - Verify that the DTO id is set to the path variable id
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("product-123");
    }

    @Test
    void testGetListResponseEntity_WithProducts() {
        // Given - Test private method via getAll
        when(productService.getAllProducts()).thenReturn(testProductList);

        // When
        ResponseEntity<List<ProductDto>> response = productController.getAll();

        // Then - Verify private method getListResponseEntity works correctly
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetListResponseEntity_WithEmptyList() {
        // Given - Test private method via getAll with empty list
        when(productService.getAllProducts()).thenReturn(new ArrayList<>());

        // When
        ResponseEntity<List<ProductDto>> response = productController.getAll();

        // Then - Verify private method returns empty list, not null
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }
}

