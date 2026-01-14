package com.portfolio.infrastructure.adapter.messaging.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notification message for queue processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String notificationId;
    private String type;
    private String recipientId;
    private String recipientEmail;
    private String subject;
    private String body;
    private Map<String, Object> templateVariableMap;
    private String priority;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
