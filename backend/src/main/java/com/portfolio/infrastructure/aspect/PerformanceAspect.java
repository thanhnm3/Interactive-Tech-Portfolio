package com.portfolio.infrastructure.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect for performance monitoring and slow query detection
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class PerformanceAspect {

    @Value("${app.performance.enabled:true}")
    private boolean isPerformanceEnabled;

    @Value("${app.performance.slow-query-threshold-ms:1000}")
    private long defaultSlowThresholdMs;

    // ==================== Pointcuts ====================

    /**
     * Pointcut for repository methods
     */
    @Pointcut("execution(* com.portfolio.infrastructure.adapter.persistence..*.*(..))")
    public void repositoryPointcut() {
        // Pointcut for repository layer
    }

    /**
     * Pointcut for methods annotated with @TrackPerformance
     */
    @Pointcut("@annotation(com.portfolio.infrastructure.aspect.TrackPerformance)")
    public void trackPerformancePointcut() {
        // Pointcut for @TrackPerformance methods
    }

    /**
     * Pointcut for external service calls
     */
    @Pointcut("execution(* com.portfolio.infrastructure.adapter.external..*.*(..))")
    public void externalServicePointcut() {
        // Pointcut for external services
    }

    // ==================== Advices ====================

    /**
     * Track repository method performance
     * @param joinPoint Method join point
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("repositoryPointcut()")
    public Object trackRepositoryPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!isPerformanceEnabled) {
            return joinPoint.proceed();
        }

        return trackMethodPerformance(joinPoint, "REPOSITORY", defaultSlowThresholdMs);
    }

    /**
     * Track methods annotated with @TrackPerformance
     * @param joinPoint Method join point
     * @param trackPerformance TrackPerformance annotation
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("@annotation(trackPerformance)")
    public Object trackAnnotatedPerformance(
            ProceedingJoinPoint joinPoint,
            TrackPerformance trackPerformance) throws Throwable {

        if (!isPerformanceEnabled) {
            return joinPoint.proceed();
        }

        String operation = trackPerformance.operation().isEmpty()
                ? joinPoint.getSignature().getName()
                : trackPerformance.operation();

        long threshold = trackPerformance.slowThresholdMs() > 0
                ? trackPerformance.slowThresholdMs()
                : defaultSlowThresholdMs;

        return trackMethodPerformance(joinPoint, operation, threshold);
    }

    /**
     * Track external service call performance
     * @param joinPoint Method join point
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("externalServicePointcut()")
    public Object trackExternalServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!isPerformanceEnabled) {
            return joinPoint.proceed();
        }

        return trackMethodPerformance(joinPoint, "EXTERNAL_SERVICE", defaultSlowThresholdMs);
    }

    // ==================== Helper Methods ====================

    /**
     * Common method for tracking performance
     * @param joinPoint Method join point
     * @param category Performance category
     * @param slowThresholdMs Threshold for slow query warning
     * @return Method result
     * @throws Throwable if method throws exception
     */
    private Object trackMethodPerformance(
            ProceedingJoinPoint joinPoint,
            String category,
            long slowThresholdMs) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        String fullMethodName = className + "." + methodName;

        long startTime = System.currentTimeMillis();
        boolean isSuccess = true;

        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            isSuccess = false;
            throw t;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logPerformanceMetrics(category, fullMethodName, executionTime, slowThresholdMs, isSuccess, joinPoint);
        }
    }

    /**
     * Log performance metrics
     * @param category Performance category
     * @param methodName Method name
     * @param executionTime Execution time in ms
     * @param slowThresholdMs Slow threshold in ms
     * @param isSuccess Whether method succeeded
     * @param joinPoint Method join point
     */
    private void logPerformanceMetrics(
            String category,
            String methodName,
            long executionTime,
            long slowThresholdMs,
            boolean isSuccess,
            ProceedingJoinPoint joinPoint) {

        String status = isSuccess ? "SUCCESS" : "FAILURE";

        if (executionTime >= slowThresholdMs) {
            String args = summarizeArguments(joinPoint.getArgs());

            log.warn("[SLOW {}] {} - {}ms (threshold: {}ms) - status: {} - args: {}",
                    category, methodName, executionTime, slowThresholdMs, status, args);
        } else {
            log.debug("[{}] {} - {}ms - status: {}", category, methodName, executionTime, status);
        }

        // Emit metrics (can be integrated with Micrometer/Prometheus)
        emitMetrics(category, methodName, executionTime, isSuccess);
    }

    /**
     * Summarize method arguments for logging
     * @param args Method arguments
     * @return Summary string
     */
    private String summarizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            Object arg = args[i];

            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String) {
                String str = (String) arg;
                sb.append(str.length() > 50 ? str.substring(0, 50) + "..." : str);
            } else if (arg instanceof Number || arg instanceof Boolean) {
                sb.append(arg);
            } else if (arg.getClass().isArray()) {
                sb.append(Arrays.toString((Object[]) arg));
            } else {
                sb.append(arg.getClass().getSimpleName());
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Emit performance metrics (placeholder for Micrometer integration)
     * @param category Metric category
     * @param methodName Method name
     * @param executionTime Execution time
     * @param isSuccess Whether call succeeded
     */
    private void emitMetrics(String category, String methodName, long executionTime, boolean isSuccess) {
        // TODO: Integrate with Micrometer for Prometheus metrics
        // Example:
        // meterRegistry.timer(category + ".execution.time", "method", methodName, "status", status)
        //     .record(executionTime, TimeUnit.MILLISECONDS);
    }
}
