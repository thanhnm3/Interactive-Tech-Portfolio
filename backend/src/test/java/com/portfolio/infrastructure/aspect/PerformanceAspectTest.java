package com.portfolio.infrastructure.aspect;

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
import static org.mockito.Mockito.*;

/**
 * Unit tests for PerformanceAspect
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PerformanceAspect Tests")
class PerformanceAspectTest {

    private PerformanceAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() throws Throwable {
        aspect = new PerformanceAspect();
        ReflectionTestUtils.setField(aspect, "isPerformanceEnabled", true);
        ReflectionTestUtils.setField(aspect, "defaultSlowThresholdMs", 1000L);

        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
    }

    @Test
    @DisplayName("Should track repository performance")
    void shouldTrackRepositoryPerformance() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.trackRepositoryPerformance(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should skip tracking when disabled")
    void shouldSkipTrackingWhenDisabled() throws Throwable {
        ReflectionTestUtils.setField(aspect, "isPerformanceEnabled", false);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.trackRepositoryPerformance(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        // No need to verify signature calls when disabled
    }

    @Test
    @DisplayName("Should track annotated method performance")
    void shouldTrackAnnotatedMethodPerformance() throws Throwable {
        TrackPerformance annotation = mock(TrackPerformance.class);
        when(annotation.operation()).thenReturn("testOperation");
        when(annotation.slowThresholdMs()).thenReturn(500L);
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.trackAnnotatedPerformance(joinPoint, annotation);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should handle exceptions in performance tracking")
    void shouldHandleExceptionsInPerformanceTracking() throws Throwable {
        RuntimeException exception = new RuntimeException("Test error");
        when(joinPoint.proceed()).thenThrow(exception);

        try {
            aspect.trackRepositoryPerformance(joinPoint);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(exception);
        }
    }
}
