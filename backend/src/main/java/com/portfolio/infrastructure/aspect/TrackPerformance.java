package com.portfolio.infrastructure.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for performance tracking
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackPerformance {

    /**
     * Operation name for tracking
     * @return Operation name
     */
    String operation() default "";

    /**
     * Threshold in milliseconds for slow query warning
     * @return Threshold in ms
     */
    long slowThresholdMs() default 1000;
}
