package com.portfolio.infrastructure.adapter.messaging.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit event message for queue processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String action;
    private String entityType;
    private String entityId;
    private Object oldValue;
    private Object newValue;
    private String ipAddress;
    private String userAgent;
    private String requestPath;
    private String requestMethod;
    private Integer responseStatus;
    private Long executionTimeMs;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
