package com.portfolio.application.port.input;

/**
 * Base interface for query use cases (read operations)
 * @param <I> Input/Query type
 * @param <O> Output/Result type
 */
public interface QueryUseCase<I, O> extends UseCase<I, O> {
    // Query use cases should not modify state
}
