package com.portfolio.infrastructure.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for audit logging
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * Action name for audit log
     * @return Action name
     */
    String action() default "";

    /**
     * Entity type being audited
     * @return Entity type name
     */
    String entityType() default "";

    /**
     * Whether to capture old value before modification
     * @return true to capture old value
     */
    boolean captureOldValue() default false;

    /**
     * Whether to capture new value after modification
     * @return true to capture new value
     */
    boolean captureNewValue() default true;
}
