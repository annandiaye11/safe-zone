package com.example.productservice.messaging;

import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductEventConsumerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductEventConsumer productEventConsumer;

    @ParameterizedTest
    @ValueSource(strings = {"user-123", "user-456", "user-789"})
    void testDeleteProduct_WithVariousUserIds_DeletesProductsForUser(String userId) {
        // Given
        doNothing().when(productService).deleteProductsByUser(userId);

        // When
        productEventConsumer.deleteProduct(userId);

        // Then
        verify(productService, times(1)).deleteProductsByUser(userId);
    }

    @Test
    void testDeleteProduct_WithNullUserId() {
        // Given
        String userId = null;
        doNothing().when(productService).deleteProductsByUser(userId);

        // When
        productEventConsumer.deleteProduct(userId);

        // Then
        verify(productService, times(1)).deleteProductsByUser(null);
    }

    @Test
    void testDeleteProduct_WithEmptyUserId() {
        // Given
        String userId = "";
        doNothing().when(productService).deleteProductsByUser(userId);

        // When
        productEventConsumer.deleteProduct(userId);

        // Then
        verify(productService, times(1)).deleteProductsByUser("");
    }

    @Test
    void testDeleteProduct_ServiceThrowsException() {
        // Given
        String userId = "user-with-error";
        doThrow(new RuntimeException("Database error"))
                .when(productService).deleteProductsByUser(userId);

        // When & Then - Exception should propagate
        assertThatThrownBy(() -> productEventConsumer.deleteProduct(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(productService, times(1)).deleteProductsByUser(userId);
    }

    @Test
    void testDeleteProduct_MultipleInvocations() {
        // Given
        String userId1 = "user-111";
        String userId2 = "user-222";
        String userId3 = "user-333";

        doNothing().when(productService).deleteProductsByUser(anyString());

        // When
        productEventConsumer.deleteProduct(userId1);
        productEventConsumer.deleteProduct(userId2);
        productEventConsumer.deleteProduct(userId3);

        // Then
        verify(productService, times(1)).deleteProductsByUser(userId1);
        verify(productService, times(1)).deleteProductsByUser(userId2);
        verify(productService, times(1)).deleteProductsByUser(userId3);
        verify(productService, times(3)).deleteProductsByUser(anyString());
    }

    @Test
    void testDeleteProduct_VerifyServiceMethodCalledWithCorrectParameter() {
        // Given
        String userId = "specific-user-id";
        doNothing().when(productService).deleteProductsByUser(userId);

        // When
        productEventConsumer.deleteProduct(userId);

        // Then - Verify exact parameter is passed
        verify(productService, times(1)).deleteProductsByUser(userId);
        verify(productService, never()).deleteProductsByUser(argThat(id -> !userId.equals(id)));
    }

    @Test
    void testDeleteProduct_KafkaListenerAnnotation() throws NoSuchMethodException {
        // When
        var method = ProductEventConsumer.class.getMethod("deleteProduct", String.class);
        var kafkaListenerAnnotation = method.getAnnotation(org.springframework.kafka.annotation.KafkaListener.class);

        // Then
        assertThat(kafkaListenerAnnotation).isNotNull();
        assertThat(kafkaListenerAnnotation.topics()).isNotEmpty();
        assertThat(kafkaListenerAnnotation.topics()[0]).isEqualTo("delete-user-products");
    }

    @Test
    void testDeleteProduct_ComponentAnnotation() {
        // When
        var componentAnnotation = ProductEventConsumer.class.getAnnotation(org.springframework.stereotype.Component.class);

        // Then
        assertThat(componentAnnotation).isNotNull();
    }

    @Test
    void testConstructor() {
        // When
        ProductEventConsumer consumer = new ProductEventConsumer(productService);

        // Then
        assertThat(consumer).isNotNull();
    }
}

