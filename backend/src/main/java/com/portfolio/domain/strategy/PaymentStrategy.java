package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;

/**
 * Strategy interface for payment processing
 * Demonstrates Strategy Pattern for interchangeable payment algorithms
 */
public interface PaymentStrategy {

    /**
     * Process payment for an order
     * @param order - order to process payment for
     * @return PaymentResult - result of payment processing
     */
    PaymentResult process(Order order);

    /**
     * Validate payment can be processed
     * @param order - order to validate
     * @return boolean - true if payment can be processed
     */
    boolean canProcess(Order order);

    /**
     * Get payment method name
     * @return String - payment method name
     */
    String getPaymentMethodName();

    /**
     * Get processing fee rate
     * @return double - fee rate (e.g., 0.03 for 3%)
     */
    double getProcessingFeeRate();
}
