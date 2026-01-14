package com.portfolio.infrastructure.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect for general logging across application layers
 */
@Aspect
@Component
@Order(3)
@Slf4j
public class LoggingAspect {

    // ==================== Pointcuts ====================

    /**
     * Pointcut for application package
     */
    @Pointcut("within(com.portfolio..*)")
    public void applicationPackagePointcut() {
        // Pointcut for application package
    }

    /**
     * Pointcut for controller layer
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
        // Pointcut for controllers
    }

    /**
     * Pointcut for service layer
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {
        // Pointcut for services
    }

    // ==================== Advices ====================

    /**
     * Log controller method entry and exit
     * @param joinPoint Method join point
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("controllerPointcut()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info(">>> CONTROLLER ENTRY: {}.{}({})",
                className, methodName, summarizeArgs(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("<<< CONTROLLER EXIT: {}.{} - {}ms - result: {}",
                    className, methodName, executionTime, summarizeResult(result));

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("!!! CONTROLLER ERROR: {}.{} - {}ms - error: {}",
                    className, methodName, executionTime, e.getMessage());

            throw e;
        }
    }

    /**
     * Log service method entry and exit
     * @param joinPoint Method join point
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("servicePointcut()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug(">>> SERVICE ENTRY: {}.{}({})",
                className, methodName, summarizeArgs(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("<<< SERVICE EXIT: {}.{} - {}ms", className, methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("!!! SERVICE ERROR: {}.{} - {}ms - error: {}",
                    className, methodName, executionTime, e.getMessage());

            throw e;
        }
    }

    /**
     * Log exceptions from application package
     * @param joinPoint Method join point
     * @param e Thrown exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Exception in {}.{}: {} - {}",
                className, methodName, e.getClass().getSimpleName(), e.getMessage());
    }

    // ==================== Helper Methods ====================

    /**
     * Summarize method arguments for logging
     * @param args Method arguments
     * @return Summary string
     */
    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        return Arrays.stream(args)
                .map(this::summarizeObject)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    /**
     * Summarize result for logging
     * @param result Method result
     * @return Summary string
     */
    private String summarizeResult(Object result) {
        return summarizeObject(result);
    }

    /**
     * Summarize object for logging (avoid large payloads)
     * @param obj Object to summarize
     * @return Summary string
     */
    private String summarizeObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        String className = obj.getClass().getSimpleName();

        if (obj instanceof String) {
            String str = (String) obj;
            return str.length() > 100 ? str.substring(0, 100) + "..." : str;
        }

        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof java.util.Collection) {
            return className + "[size=" + ((java.util.Collection<?>) obj).size() + "]";
        }

        if (obj.getClass().isArray()) {
            return className + "[length=" + java.lang.reflect.Array.getLength(obj) + "]";
        }

        return className;
    }
}
