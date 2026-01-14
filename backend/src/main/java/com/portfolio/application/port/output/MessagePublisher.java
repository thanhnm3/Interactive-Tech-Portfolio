package com.portfolio.application.port.output;

/**
 * Message publisher port for sending messages to message queue
 */
public interface MessagePublisher {

    /**
     * Publish a message to specified exchange
     * @param exchange Exchange name
     * @param routingKey Routing key
     * @param message Message object
     */
    void publish(String exchange, String routingKey, Object message);

    /**
     * Publish a message with delay
     * @param exchange Exchange name
     * @param routingKey Routing key
     * @param message Message object
     * @param delayMs Delay in milliseconds
     */
    void publishWithDelay(String exchange, String routingKey, Object message, long delayMs);
}
