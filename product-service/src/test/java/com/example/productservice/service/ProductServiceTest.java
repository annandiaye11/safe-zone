package com.example.productservice.service;

import com.example.productservice.data.entities.Product;
import com.example.productservice.data.repositories.ProductRepository;
import com.example.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("1");
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        testProduct.setUserId("user123");
    }

    @Test
    void testCreateProduct_Success() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.create(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetById_Found() {
        // Given
        when(productRepository.findById("1")).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getById("1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        // When
        Product result = productService.getById("999");

        // Then
        assertThat(result).isNull();
        verify(productRepository, times(1)).findById("999");
    }

    @Test
    void testGetAllProducts_Success() {
        // Given
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Product 2");
        List<Product> products = Arrays.asList(testProduct, product2);

        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertThat(result).hasSize(2);
        verify(productRepository, times(1)).findAll(); // Correction: 1 seul appel
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of());

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertThat(result).isEmpty();
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        testProduct.setPrice(150.0);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.update(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(150.0);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        doNothing().when(productRepository).deleteById("1");

        // When
        productService.delete("1");

        // Then
        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    void testGetByUserId_Found() {
        // Given
        List<Product> userProducts = Collections.singletonList(testProduct);
        when(productRepository.findByUserId("user123")).thenReturn(userProducts);

        // When
        List<Product> result = productService.getByUserId("user123");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");
        verify(productRepository, times(1)).findByUserId("user123");
    }

    @Test
    void testGetByUserId_NotFound() {
        // Given
        when(productRepository.findByUserId("user999")).thenReturn(List.of());

        // When
        List<Product> result = productService.getByUserId("user999");

        // Then
        assertThat(result).isEmpty(); // Correction: retourne une liste vide, pas null
        verify(productRepository, times(1)).findByUserId("user999");
    }

    @Test
    void testDeleteProductsByUser_Success() {
        // Given
        doNothing().when(productRepository).deleteProductsByUserId("user123");

        // When
        productService.deleteProductsByUser("user123");

        // Then
        verify(productRepository, times(1)).deleteProductsByUserId("user123");
    }

    @Test
    void testGetByName_Found() {
        // Given
        when(productRepository.getByName("Test Product")).thenReturn(testProduct);

        // When
        Product result = productService.getByName("Test Product");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).getByName("Test Product");
    }

    @Test
    void testGetByName_NotFound() {
        // Given
        when(productRepository.getByName("Unknown Product")).thenReturn(null);

        // When
        Product result = productService.getByName("Unknown Product");

        // Then
        assertThat(result).isNull();
        verify(productRepository, times(1)).getByName("Unknown Product");
    }
}
