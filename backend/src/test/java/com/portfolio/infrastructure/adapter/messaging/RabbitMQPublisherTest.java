package com.portfolio.infrastructure.adapter.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RabbitMQPublisher
 */
@DisplayName("RabbitMQPublisher Tests")
class RabbitMQPublisherTest {

    private RabbitMQPublisher publisher;
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new RabbitMQPublisher(rabbitTemplate);
    }

    @Test
    @DisplayName("Should publish message to exchange")
    void shouldPublishMessageToExchange() {
        String exchange = "test.exchange";
        String routingKey = "test.key";
        Object message = "test message";

        publisher.publish(exchange, routingKey, message);

        verify(rabbitTemplate).convertAndSend(eq(exchange), eq(routingKey), eq(message));
    }

    @Test
    @DisplayName("Should publish message with delay")
    void shouldPublishMessageWithDelay() {
        String exchange = "test.exchange";
        String routingKey = "test.key";
        Object message = "test message";
        long delayMs = 5000L;

        publisher.publishWithDelay(exchange, routingKey, message, delayMs);

        verify(rabbitTemplate).convertAndSend(
            eq(exchange),
            eq(routingKey),
            eq(message),
            any(MessagePostProcessor.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when publish fails")
    void shouldThrowExceptionWhenPublishFails() {
        String exchange = "test.exchange";
        String routingKey = "test.key";
        Object message = "test message";

        doThrow(new RuntimeException("Connection failed"))
            .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        try {
            publisher.publish(exchange, routingKey, message);
        } catch (RuntimeException e) {
            assert e.getMessage().contains("Failed to publish message");
        }
    }
}
