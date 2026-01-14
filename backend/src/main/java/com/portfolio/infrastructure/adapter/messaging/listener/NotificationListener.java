package com.portfolio.infrastructure.adapter.messaging.listener;

import com.portfolio.infrastructure.adapter.messaging.dto.NotificationMessage;
import com.portfolio.shared.constant.QueueName;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Listener for notification messages
 */
@Component
@Slf4j
public class NotificationListener {

    /**
     * Process notification messages
     * @param message Notification message
     * @param channel RabbitMQ channel
     * @param deliveryTag Message delivery tag
     */
    @RabbitListener(queues = QueueName.NOTIFICATION_QUEUE)
    public void handleNotification(
            NotificationMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        try {
            log.info("Received notification: type={}, recipient={}",
                    message.getType(), message.getRecipientEmail());

            // Process notification
            processNotification(message);

            // Acknowledge message
            channel.basicAck(deliveryTag, false);
            log.debug("Notification processed successfully: {}", message.getNotificationId());

        } catch (Exception e) {
            log.error("Failed to process notification: {}", e.getMessage(), e);

            try {
                // Reject and don't requeue (will go to DLQ)
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("Failed to nack message: {}", ex.getMessage());
            }
        }
    }

    /**
     * Process notification (send email, push notification, etc.)
     * @param message Notification message
     */
    private void processNotification(NotificationMessage message) {
        // TODO: Implement actual notification sending
        log.debug("Processing notification: id={}, type={}, priority={}",
                message.getNotificationId(), message.getType(), message.getPriority());
    }
}
