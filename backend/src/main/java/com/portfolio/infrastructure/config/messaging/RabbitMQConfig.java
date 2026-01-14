package com.portfolio.infrastructure.config.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portfolio.shared.constant.QueueName;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for message queuing
 */
@Configuration
public class RabbitMQConfig {

    // ==================== Exchanges ====================

    /**
     * Audit events exchange
     * @return DirectExchange for audit events
     */
    @Bean
    public DirectExchange auditExchange() {
        return new DirectExchange(QueueName.AUDIT_EXCHANGE, true, false);
    }

    /**
     * Notification events exchange
     * @return DirectExchange for notifications
     */
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(QueueName.NOTIFICATION_EXCHANGE, true, false);
    }

    /**
     * Dead letter exchange for failed messages
     * @return DirectExchange for dead letters
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(QueueName.DEAD_LETTER_EXCHANGE, true, false);
    }

    // ==================== Queues ====================

    /**
     * Audit queue with dead letter configuration
     * @return Queue for audit events
     */
    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(QueueName.AUDIT_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueName.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", QueueName.DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 86400000)  // 24 hours TTL
                .build();
    }

    /**
     * Notification queue with dead letter configuration
     * @return Queue for notifications
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QueueName.NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", QueueName.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", QueueName.DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 3600000)  // 1 hour TTL
                .build();
    }

    /**
     * Dead letter queue for failed messages
     * @return Queue for dead letters
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QueueName.DEAD_LETTER_QUEUE)
                .withArgument("x-message-ttl", 604800000)  // 7 days TTL
                .build();
    }

    // ==================== Bindings ====================

    /**
     * Bind audit queue to audit exchange
     * @param auditQueue Audit queue
     * @param auditExchange Audit exchange
     * @return Binding configuration
     */
    @Bean
    public Binding auditBinding(Queue auditQueue, DirectExchange auditExchange) {
        return BindingBuilder.bind(auditQueue)
                .to(auditExchange)
                .with(QueueName.AUDIT_ROUTING_KEY);
    }

    /**
     * Bind notification queue to notification exchange
     * @param notificationQueue Notification queue
     * @param notificationExchange Notification exchange
     * @return Binding configuration
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(QueueName.NOTIFICATION_ROUTING_KEY);
    }

    /**
     * Bind dead letter queue to dead letter exchange
     * @param deadLetterQueue Dead letter queue
     * @param deadLetterExchange Dead letter exchange
     * @return Binding configuration
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(QueueName.DEAD_LETTER_ROUTING_KEY);
    }

    // ==================== Message Converter ====================

    /**
     * JSON message converter for RabbitMQ
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // ==================== RabbitTemplate ====================

    /**
     * Configure RabbitTemplate with JSON converter
     * @param connectionFactory RabbitMQ connection factory
     * @param jsonMessageConverter JSON message converter
     * @return Configured RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    // ==================== Listener Container Factory ====================

    /**
     * Configure listener container factory
     * @param connectionFactory RabbitMQ connection factory
     * @param jsonMessageConverter JSON message converter
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setPrefetchCount(10);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
}
