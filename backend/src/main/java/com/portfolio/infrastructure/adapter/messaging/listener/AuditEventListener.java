package com.portfolio.infrastructure.adapter.messaging.listener;

import com.portfolio.infrastructure.adapter.messaging.dto.AuditEventMessage;
import com.portfolio.shared.constant.QueueName;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Listener for audit event messages
 */
@Component
@Slf4j
public class AuditEventListener {

    /**
     * Process audit event messages
     * @param message Audit event message
     * @param channel RabbitMQ channel
     * @param deliveryTag Message delivery tag
     */
    @RabbitListener(queues = QueueName.AUDIT_QUEUE)
    public void handleAuditEvent(
            AuditEventMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        try {
            log.info("Received audit event: action={}, entityType={}, entityId={}",
                    message.getAction(), message.getEntityType(), message.getEntityId());

            // TODO: Save audit event to database
            processAuditEvent(message);

            // Acknowledge message
            channel.basicAck(deliveryTag, false);
            log.debug("Audit event processed successfully: {}", message.getAction());

        } catch (Exception e) {
            log.error("Failed to process audit event: {}", e.getMessage(), e);

            try {
                // Reject and don't requeue (will go to DLQ)
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("Failed to nack message: {}", ex.getMessage());
            }
        }
    }

    /**
     * Process audit event (save to database, etc.)
     * @param message Audit event message
     */
    private void processAuditEvent(AuditEventMessage message) {
        // TODO: Implement actual audit log persistence
        log.debug("Processing audit event: userId={}, action={}, executionTime={}ms",
                message.getUserId(), message.getAction(), message.getExecutionTimeMs());
    }
}
