package com.portfolio.infrastructure.aspect;

import com.portfolio.application.port.output.MessagePublisher;
import com.portfolio.infrastructure.adapter.messaging.dto.AuditEventMessage;
import com.portfolio.shared.constant.QueueName;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP Aspect for audit logging of API calls and business operations
 */
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

    private final MessagePublisher messagePublisher;

    @Value("${app.audit.enabled:true}")
    private boolean isAuditEnabled;

    // ==================== Pointcuts ====================

    /**
     * Pointcut for all REST controller methods
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
        // Pointcut for REST controllers
    }

    /**
     * Pointcut for methods annotated with @Auditable
     */
    @Pointcut("@annotation(com.portfolio.infrastructure.aspect.Auditable)")
    public void auditablePointcut() {
        // Pointcut for @Auditable methods
    }

    /**
     * Pointcut for service layer methods
     */
    @Pointcut("execution(* com.portfolio.application.service..*.*(..))")
    public void serviceLayerPointcut() {
        // Pointcut for service layer
    }

    // ==================== Advices ====================

    /**
     * Audit all REST controller method invocations
     * @param joinPoint Method join point
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("restControllerPointcut()")
    public Object auditRestController(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!isAuditEnabled) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        HttpServletRequest request = getCurrentRequest();
        String requestPath = request != null ? request.getRequestURI() : "unknown";
        String requestMethod = request != null ? request.getMethod() : "unknown";
        String ipAddress = request != null ? getClientIpAddress(request) : "unknown";
        String userAgent = request != null ? request.getHeader("User-Agent") : "unknown";

        log.debug("REST API Call: {}.{} - {} {}", className, methodName, requestMethod, requestPath);

        Object result = null;
        int responseStatus = 200;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            responseStatus = 500;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            // Send audit event asynchronously
            sendAuditEvent(AuditEventMessage.builder()
                    .action(requestMethod + "_" + methodName)
                    .entityType(className)
                    .requestPath(requestPath)
                    .requestMethod(requestMethod)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .responseStatus(responseStatus)
                    .executionTimeMs(executionTime)
                    .build());

            log.info("REST API Completed: {}.{} - {} {} - {}ms",
                    className, methodName, requestMethod, requestPath, executionTime);
        }
    }

    /**
     * Audit methods annotated with @Auditable
     * @param joinPoint Method join point
     * @param auditable Auditable annotation
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("@annotation(auditable)")
    public Object auditAnnotatedMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        if (!isAuditEnabled) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String action = auditable.action().isEmpty() ? methodName : auditable.action();
        String entityType = auditable.entityType().isEmpty() ? className : auditable.entityType();

        Object[] args = joinPoint.getArgs();
        String entityId = extractEntityId(args);

        log.debug("Auditable method: {}.{} - action: {}, entityType: {}",
                className, methodName, action, entityType);

        Object oldValue = null;
        if (auditable.captureOldValue()) {
            // TODO: Implement old value capture if needed
            oldValue = "captured_before";
        }

        Object result = null;

        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            Object newValue = null;
            if (auditable.captureNewValue() && result != null) {
                newValue = result;
            }

            sendAuditEvent(AuditEventMessage.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(auditable.captureNewValue() ? "captured_after" : null)
                    .executionTimeMs(executionTime)
                    .build());

            log.info("Auditable method completed: {}.{} - {}ms", className, methodName, executionTime);
        }
    }

    /**
     * Log successful service method execution
     * @param joinPoint Method join point
     * @param result Method result
     */
    @AfterReturning(pointcut = "serviceLayerPointcut()", returning = "result")
    public void logServiceSuccess(JoinPoint joinPoint, Object result) {
        if (!isAuditEnabled) {
            return;
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.debug("Service method success: {}.{}", className, methodName);
    }

    /**
     * Log service method exceptions
     * @param joinPoint Method join point
     * @param exception Thrown exception
     */
    @AfterThrowing(pointcut = "serviceLayerPointcut()", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.error("Service method exception: {}.{} - args: {} - error: {}",
                className, methodName, args, exception.getMessage());
    }

    // ==================== Helper Methods ====================

    /**
     * Get current HTTP request from context
     * @return HttpServletRequest or null
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract client IP address from request
     * @param request HTTP request
     * @return Client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");

        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Extract entity ID from method arguments
     * @param args Method arguments
     * @return Entity ID string or null
     */
    private String extractEntityId(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        // Try to extract ID from first argument
        Object firstArg = args[0];

        if (firstArg instanceof String) {
            return (String) firstArg;
        }

        if (firstArg instanceof Long) {
            return firstArg.toString();
        }

        if (firstArg instanceof java.util.UUID) {
            return firstArg.toString();
        }

        return null;
    }

    /**
     * Send audit event to message queue
     * @param event Audit event message
     */
    private void sendAuditEvent(AuditEventMessage event) {
        try {
            messagePublisher.publish(
                    QueueName.AUDIT_EXCHANGE,
                    QueueName.AUDIT_ROUTING_KEY,
                    event);
        } catch (Exception e) {
            log.error("Failed to send audit event: {}", e.getMessage());
            // Don't throw - audit failure should not break the main flow
        }
    }
}
