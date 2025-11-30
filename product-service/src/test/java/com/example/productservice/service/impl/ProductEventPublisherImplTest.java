package com.example.productservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductEventPublisherImplTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ProductEventPublisherImpl productEventPublisher;

    @ParameterizedTest
    @ValueSource(strings = {"product-123", "product-456", "product-789"})
    void testSendDeleteEvent_WithVariousProductIds_SendsToKafka(String productId) {
        // Given
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        productEventPublisher.sendDeleteEvent(productId);

        // Then
        verify(kafkaTemplate, times(1)).send("delete-product-media", productId);
    }

    @Test
    void testSendDeleteEvent_VerifyTopicAndProductId() {
        // Given
        String productId = "specific-product-123";
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> productIdCaptor = ArgumentCaptor.forClass(String.class);

        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        productEventPublisher.sendDeleteEvent(productId);

        // Then
        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), productIdCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("delete-product-media");
        assertThat(productIdCaptor.getValue()).isEqualTo("specific-product-123");
    }

    @Test
    void testSendDeleteEvent_WithNullProductId() {
        // Given
        String productId = null;
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        productEventPublisher.sendDeleteEvent(productId);

        // Then
        verify(kafkaTemplate, times(1)).send("delete-product-media", null);
    }

    @Test
    void testSendDeleteEvent_WithEmptyProductId() {
        // Given
        String productId = "";
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        productEventPublisher.sendDeleteEvent(productId);

        // Then
        verify(kafkaTemplate, times(1)).send("delete-product-media", "");
    }

    @Test
    void testSendDeleteEvent_KafkaTemplateThrowsException() {
        // Given
        String productId = "product-error";
        when(kafkaTemplate.send(anyString(), nullable(String.class)))
                .thenThrow(new RuntimeException("Kafka connection error"));

        // When & Then
        assertThatThrownBy(() -> productEventPublisher.sendDeleteEvent(productId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Kafka connection error");

        verify(kafkaTemplate, times(1)).send("delete-product-media", productId);
    }

    @Test
    void testSendDeleteEvent_MultipleInvocations() {
        // Given
        String productId1 = "product-111";
        String productId2 = "product-222";
        String productId3 = "product-333";

        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        productEventPublisher.sendDeleteEvent(productId1);
        productEventPublisher.sendDeleteEvent(productId2);
        productEventPublisher.sendDeleteEvent(productId3);

        // Then
        verify(kafkaTemplate, times(3)).send(eq("delete-product-media"), nullable(String.class));
        verify(kafkaTemplate, times(1)).send("delete-product-media", productId1);
        verify(kafkaTemplate, times(1)).send("delete-product-media", productId2);
        verify(kafkaTemplate, times(1)).send("delete-product-media", productId3);
    }

    @Test
    void testSendDeleteEvent_VerifyCorrectTopic() {
        // Given
        String productId = "test-product";
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        productEventPublisher.sendDeleteEvent(productId);

        // Then - Verify topic is exactly "delete-product-media"
        verify(kafkaTemplate, times(1)).send(eq("delete-product-media"), nullable(String.class));
        verify(kafkaTemplate, never()).send(eq("wrong-topic"), nullable(String.class));
    }

    @Test
    void testSendDeleteEvent_ServiceAnnotation() {
        // When
        var serviceAnnotation = ProductEventPublisherImpl.class.getAnnotation(org.springframework.stereotype.Service.class);

        // Then
        assertThat(serviceAnnotation).isNotNull();
    }

    @Test
    void testConstructor() {
        // When
        ProductEventPublisherImpl publisher = new ProductEventPublisherImpl(kafkaTemplate);

        // Then
        assertThat(publisher).isNotNull();
    }

    @Test
    void testImplementsInterface() {
        // Then
        assertThat(productEventPublisher)
                .isInstanceOf(com.example.productservice.service.ProductEventPublisher.class);
    }
}

