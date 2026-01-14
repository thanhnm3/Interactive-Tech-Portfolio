package com.portfolio.application.port.input;

/**
 * Base marker interface for all use cases (input ports)
 * Use cases represent the application's business operations
 * @param <I> Input type for the use case
 * @param <O> Output type for the use case
 */
public interface UseCase<I, O> {

    /**
     * Execute the use case
     * @param input Input data for the use case
     * @return Output result from the use case
     */
    O execute(I input);
}
