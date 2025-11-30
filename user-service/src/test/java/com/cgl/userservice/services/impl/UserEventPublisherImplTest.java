package com.cgl.userservice.services.impl;

import com.cgl.userservice.services.UserEventPublisher;
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
class UserEventPublisherImplTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private UserEventPublisherImpl userEventPublisher;

    @ParameterizedTest
    @ValueSource(strings = {"user-123", "user-456", "user-789"})
    void testSendDeleteEvent_WithVariousUserIds_SendsToKafka(String userId) {
        // Given
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId);

        // Then
        verify(kafkaTemplate, times(1)).send("delete-user-products", userId);
    }

    @Test
    void testSendDeleteEvent_VerifyTopicAndUserId() {
        // Given
        String userId = "specific-user-123";
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);

        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId);

        // Then
        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), userIdCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("delete-user-products");
        assertThat(userIdCaptor.getValue()).isEqualTo("specific-user-123");
    }

    @Test
    void testSendDeleteEvent_WithNullUserId() {
        // Given
        String userId = null;
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId);

        // Then
        verify(kafkaTemplate, times(1)).send("delete-user-products", null);
    }

    @Test
    void testSendDeleteEvent_WithEmptyUserId() {
        // Given
        String userId = "";
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId);

        // Then
        verify(kafkaTemplate, times(1)).send("delete-user-products", "");
    }

    @Test
    void testSendDeleteEvent_KafkaTemplateThrowsException() {
        // Given
        String userId = "user-error";
        when(kafkaTemplate.send(anyString(), nullable(String.class)))
                .thenThrow(new RuntimeException("Kafka connection error"));

        // When & Then
        assertThatThrownBy(() -> userEventPublisher.sendDeleteEvent(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Kafka connection error");

        verify(kafkaTemplate, times(1)).send("delete-user-products", userId);
    }

    @Test
    void testSendDeleteEvent_MultipleInvocations() {
        // Given
        String userId1 = "user-111";
        String userId2 = "user-222";
        String userId3 = "user-333";

        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId1);
        userEventPublisher.sendDeleteEvent(userId2);
        userEventPublisher.sendDeleteEvent(userId3);

        // Then
        verify(kafkaTemplate, times(3)).send(eq("delete-user-products"), nullable(String.class));
        verify(kafkaTemplate, times(1)).send("delete-user-products", userId1);
        verify(kafkaTemplate, times(1)).send("delete-user-products", userId2);
        verify(kafkaTemplate, times(1)).send("delete-user-products", userId3);
    }

    @Test
    void testSendDeleteEvent_VerifyCorrectTopic() {
        // Given
        String userId = "test-user";
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId);

        // Then - Verify topic is exactly "delete-user-products"
        verify(kafkaTemplate, times(1)).send(eq("delete-user-products"), nullable(String.class));
        verify(kafkaTemplate, never()).send(eq("wrong-topic"), nullable(String.class));
    }

    @Test
    void testServiceAnnotation() {
        // When
        var serviceAnnotation = UserEventPublisherImpl.class.getAnnotation(org.springframework.stereotype.Service.class);

        // Then
        assertThat(serviceAnnotation).isNotNull();
    }

    @Test
    void testConstructor() {
        // When
        UserEventPublisherImpl publisher = new UserEventPublisherImpl(kafkaTemplate);

        // Then
        assertThat(publisher).isNotNull();
    }

    @Test
    void testImplementsInterface() {
        // Then
        assertThat(userEventPublisher).isInstanceOf(UserEventPublisher.class);
    }

    @Test
    void testSendDeleteEvent_VerifyExactParameters() {
        // Given
        String userId = "exact-user-id";
        when(kafkaTemplate.send(anyString(), nullable(String.class))).thenReturn(null);

        // When
        userEventPublisher.sendDeleteEvent(userId);

        // Then - Verify exact parameters
        verify(kafkaTemplate, times(1)).send("delete-user-products", userId);
        verify(kafkaTemplate, never()).send(argThat(topic -> !"delete-user-products".equals(topic)), anyString());
    }
}

