package com.portfolio.infrastructure.config.database;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aspect for automatic datasource routing based on transaction type
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class DataSourceRoutingAspect {

    /**
     * Route to replica for methods annotated with ReadOnlyConnection
     * @param joinPoint Method join point
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("@annotation(com.portfolio.infrastructure.config.database.ReadOnlyConnection)")
    public Object routeToReplica(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.debug("Routing to REPLICA datasource for: {}", joinPoint.getSignature().getName());
            DataSourceContextHolder.useReplica();
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * Route based on transaction read-only flag
     * @param joinPoint Method join point
     * @param transactional Transactional annotation
     * @return Method result
     * @throws Throwable if method throws exception
     */
    @Around("@annotation(transactional)")
    public Object routeByTransaction(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                log.debug("Routing to REPLICA datasource (readOnly=true) for: {}", 
                        joinPoint.getSignature().getName());
                DataSourceContextHolder.useReplica();
            } else {
                log.debug("Routing to PRIMARY datasource for: {}", joinPoint.getSignature().getName());
                DataSourceContextHolder.usePrimary();
            }
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
