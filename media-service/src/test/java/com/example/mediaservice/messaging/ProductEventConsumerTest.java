package com.example.mediaservice.messaging;

import com.example.mediaservice.services.MediaService;
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
    private MediaService mediaService;

    @InjectMocks
    private ProductEventConsumer productEventConsumer;

    @ParameterizedTest
    @ValueSource(strings = {"product-123", "product-456", "product-789"})
    void testDeleteProduct_WithVariousProductIds_DeletesMediaForProduct(String productId) {
        // Given
        doNothing().when(mediaService).deleteMediaByProductId(productId);

        // When
        productEventConsumer.deleteProduct(productId);

        // Then
        verify(mediaService, times(1)).deleteMediaByProductId(productId);
    }

    @Test
    void testDeleteProduct_WithNullProductId() {
        // Given
        String productId = null;
        doNothing().when(mediaService).deleteMediaByProductId(productId);

        // When
        productEventConsumer.deleteProduct(productId);

        // Then
        verify(mediaService, times(1)).deleteMediaByProductId(null);
    }

    @Test
    void testDeleteProduct_WithEmptyProductId() {
        // Given
        String productId = "";
        doNothing().when(mediaService).deleteMediaByProductId(productId);

        // When
        productEventConsumer.deleteProduct(productId);

        // Then
        verify(mediaService, times(1)).deleteMediaByProductId("");
    }

    @Test
    void testDeleteProduct_ServiceThrowsException() {
        // Given
        String productId = "product-with-error";
        doThrow(new RuntimeException("Database error"))
                .when(mediaService).deleteMediaByProductId(productId);

        // When & Then - Exception should propagate
        assertThatThrownBy(() -> productEventConsumer.deleteProduct(productId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(mediaService, times(1)).deleteMediaByProductId(productId);
    }

    @Test
    void testDeleteProduct_MultipleInvocations() {
        // Given
        String productId1 = "product-111";
        String productId2 = "product-222";
        String productId3 = "product-333";

        doNothing().when(mediaService).deleteMediaByProductId(anyString());

        // When
        productEventConsumer.deleteProduct(productId1);
        productEventConsumer.deleteProduct(productId2);
        productEventConsumer.deleteProduct(productId3);

        // Then
        verify(mediaService, times(1)).deleteMediaByProductId(productId1);
        verify(mediaService, times(1)).deleteMediaByProductId(productId2);
        verify(mediaService, times(1)).deleteMediaByProductId(productId3);
        verify(mediaService, times(3)).deleteMediaByProductId(anyString());
    }

    @Test
    void testDeleteProduct_VerifyServiceMethodCalledWithCorrectParameter() {
        // Given
        String productId = "specific-product-id";
        doNothing().when(mediaService).deleteMediaByProductId(productId);

        // When
        productEventConsumer.deleteProduct(productId);

        // Then - Verify exact parameter is passed
        verify(mediaService, times(1)).deleteMediaByProductId(productId);
        verify(mediaService, never()).deleteMediaByProductId(argThat(id -> !productId.equals(id)));
    }

    @Test
    void testDeleteProduct_KafkaListenerAnnotation() throws NoSuchMethodException {
        // When
        var method = ProductEventConsumer.class.getMethod("deleteProduct", String.class);
        var kafkaListenerAnnotation = method.getAnnotation(org.springframework.kafka.annotation.KafkaListener.class);

        // Then
        assertThat(kafkaListenerAnnotation).isNotNull();
        assertThat(kafkaListenerAnnotation.topics()).isNotEmpty();
        assertThat(kafkaListenerAnnotation.topics()[0]).isEqualTo("delete-product-media");
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
        ProductEventConsumer consumer = new ProductEventConsumer(mediaService);

        // Then
        assertThat(consumer).isNotNull();
    }
}

