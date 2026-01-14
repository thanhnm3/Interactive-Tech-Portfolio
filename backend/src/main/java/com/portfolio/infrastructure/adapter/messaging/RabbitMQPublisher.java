package com.portfolio.infrastructure.adapter.messaging;

import com.portfolio.application.port.output.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ message publisher implementation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisher implements MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publish message to specified exchange
     * @param exchange Exchange name
     * @param routingKey Routing key
     * @param message Message object
     */
    @Override
    public void publish(String exchange, String routingKey, Object message) {
        try {
            log.debug("Publishing message to exchange: {}, routingKey: {}", exchange, routingKey);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("Message published successfully to {}/{}", exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish message to {}/{}: {}", exchange, routingKey, e.getMessage(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    /**
     * Publish message with delay
     * @param exchange Exchange name
     * @param routingKey Routing key
     * @param message Message object
     * @param delayMs Delay in milliseconds
     */
    @Override
    public void publishWithDelay(String exchange, String routingKey, Object message, long delayMs) {
        try {
            log.debug("Publishing delayed message to exchange: {}, routingKey: {}, delay: {}ms", 
                    exchange, routingKey, delayMs);

            MessagePostProcessor messagePostProcessor = msg -> {
                msg.getMessageProperties().setDelay((int) delayMs);
                return msg;
            };

            rabbitTemplate.convertAndSend(exchange, routingKey, message, messagePostProcessor);
            log.info("Delayed message published successfully to {}/{} with delay {}ms", 
                    exchange, routingKey, delayMs);
        } catch (Exception e) {
            log.error("Failed to publish delayed message to {}/{}: {}", 
                    exchange, routingKey, e.getMessage(), e);
            throw new RuntimeException("Failed to publish delayed message", e);
        }
    }
}
