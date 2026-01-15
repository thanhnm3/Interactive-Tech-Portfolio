package com.portfolio.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingAspect
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingAspect Tests")
class LoggingAspectTest {

    private LoggingAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setUp() throws Throwable {
        aspect = new LoggingAspect();

        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getArgs()).thenReturn(new Object[] {});
    }

    @Test
    @DisplayName("Should log controller method entry and exit")
    void shouldLogControllerMethodEntryAndExit() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.logControllerMethods(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should log service method entry and exit")
    void shouldLogServiceMethodEntryAndExit() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = aspect.logServiceMethods(joinPoint);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should log exceptions")
    void shouldLogExceptions() throws Throwable {
        RuntimeException exception = new RuntimeException("Test error");
        when(joinPoint.proceed()).thenThrow(exception);

        try {
            aspect.logControllerMethods(joinPoint);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(exception);
        }
    }
}
