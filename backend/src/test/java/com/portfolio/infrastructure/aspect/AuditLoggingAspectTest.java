package com.portfolio.infrastructure.aspect;

import com.portfolio.application.port.output.MessagePublisher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditLoggingAspect
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuditLoggingAspect Tests")
class AuditLoggingAspectTest {

    private AuditLoggingAspect aspect;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() throws Throwable {
        aspect = new AuditLoggingAspect(messagePublisher);
        ReflectionTestUtils.setField(aspect, "isAuditEnabled", true);

        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
    }

    @Test
    @DisplayName("Should audit REST controller method")
    void shouldAuditRestControllerMethod() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.auditRestController(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        verify(messagePublisher, atLeastOnce()).publish(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should skip audit when disabled")
    void shouldSkipAuditWhenDisabled() throws Throwable {
        ReflectionTestUtils.setField(aspect, "isAuditEnabled", false);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.auditRestController(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        verify(messagePublisher, never()).publish(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should audit annotated method")
    void shouldAuditAnnotatedMethod() throws Throwable {
        Auditable annotation = mock(Auditable.class);
        when(annotation.action()).thenReturn("testAction");
        when(annotation.entityType()).thenReturn("TestEntity");
        when(annotation.captureOldValue()).thenReturn(false);
        when(annotation.captureNewValue()).thenReturn(false);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.auditAnnotatedMethod(joinPoint, annotation);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should handle exceptions in audit")
    void shouldHandleExceptionsInAudit() throws Throwable {
        RuntimeException exception = new RuntimeException("Test error");
        when(joinPoint.proceed()).thenThrow(exception);

        try {
            aspect.auditRestController(joinPoint);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(exception);
        }
    }
}
