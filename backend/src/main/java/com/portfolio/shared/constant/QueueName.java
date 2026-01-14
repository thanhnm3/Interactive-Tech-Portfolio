package com.portfolio.shared.constant;

/**
 * Message queue name constants
 */
public final class QueueName {

    private QueueName() {
        // Prevent instantiation
    }

    // Exchange names
    public static final String AUDIT_EXCHANGE = "portfolio.audit.exchange";
    public static final String NOTIFICATION_EXCHANGE = "portfolio.notification.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "portfolio.dlx.exchange";

    // Queue names
    public static final String AUDIT_QUEUE = "portfolio.audit.queue";
    public static final String NOTIFICATION_QUEUE = "portfolio.notification.queue";
    public static final String DEAD_LETTER_QUEUE = "portfolio.dlq.queue";

    // Routing keys
    public static final String AUDIT_ROUTING_KEY = "audit.event";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.event";
    public static final String DEAD_LETTER_ROUTING_KEY = "dlq.event";
}
