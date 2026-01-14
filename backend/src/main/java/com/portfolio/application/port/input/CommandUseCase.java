package com.portfolio.application.port.input;

/**
 * Base interface for command use cases (write operations)
 * @param <I> Input/Command type
 * @param <O> Output/Result type
 */
public interface CommandUseCase<I, O> extends UseCase<I, O> {
    // Command use cases modify state
}
