package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payment context for Strategy Pattern
 * Manages payment strategies and delegates payment processing
 */
@Component
public class PaymentContext {

    private final Map<String, PaymentStrategy> strategies;
    private PaymentStrategy currentStrategy;

    /**
     * Constructor with dependency injection of all payment strategies
     * @param creditCardPayment - credit card strategy
     * @param payPayPayment - PayPay strategy
     * @param bankTransferPayment - bank transfer strategy
     */
    public PaymentContext(CreditCardPayment creditCardPayment,
                         PayPayPayment payPayPayment,
                         BankTransferPayment bankTransferPayment) {
        this.strategies = new HashMap<>();
        this.strategies.put("CREDIT_CARD", creditCardPayment);
        this.strategies.put("PAYPAY", payPayPayment);
        this.strategies.put("BANK_TRANSFER", bankTransferPayment);

        // Default to credit card
        this.currentStrategy = creditCardPayment;
    }

    /**
     * Set current payment strategy by method name
     * @param paymentMethod - payment method name
     * @throws IllegalArgumentException if payment method not found
     */
    public void setStrategy(String paymentMethod) {
        PaymentStrategy strategy = strategies.get(paymentMethod.toUpperCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
        }

        this.currentStrategy = strategy;
    }

    /**
     * Set current payment strategy directly
     * @param strategy - payment strategy
     */
    public void setStrategy(PaymentStrategy strategy) {
        this.currentStrategy = strategy;
    }

    /**
     * Execute payment using current strategy
     * @param order - order to process
     * @return PaymentResult - payment result
     */
    public PaymentResult executePayment(Order order) {
        if (currentStrategy == null) {
            return PaymentResult.failure(
                "NO_STRATEGY",
                "No payment strategy configured",
                "UNKNOWN"
            );
        }

        return currentStrategy.process(order);
    }

    /**
     * Execute payment with specified strategy
     * @param order - order to process
     * @param paymentMethod - payment method to use
     * @return PaymentResult - payment result
     */
    public PaymentResult executePayment(Order order, String paymentMethod) {
        setStrategy(paymentMethod);
        return executePayment(order);
    }

    /**
     * Get available payment methods
     * @return List - available payment method names
     */
    public List<String> getAvailablePaymentMethods() {
        return List.copyOf(strategies.keySet());
    }

    /**
     * Get payment method details
     * @return List - payment method details
     */
    public List<Map<String, Object>> getPaymentMethodDetails() {
        return strategies.values().stream()
            .map(strategy -> Map.<String, Object>of(
                "name", strategy.getPaymentMethodName(),
                "feeRate", strategy.getProcessingFeeRate(),
                "feePercentage", String.format("%.1f%%", strategy.getProcessingFeeRate() * 100)
            ))
            .toList();
    }

    /**
     * Check if order can be processed with any available method
     * @param order - order to check
     * @return List - available payment methods for order
     */
    public List<String> getAvailableMethodsForOrder(Order order) {
        return strategies.entrySet().stream()
            .filter(entry -> entry.getValue().canProcess(order))
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Get current strategy name
     * @return String - current strategy name
     */
    public String getCurrentStrategyName() {
        return currentStrategy != null ? currentStrategy.getPaymentMethodName() : null;
    }

    /**
     * Get strategy by name
     * @param paymentMethod - payment method name
     * @return PaymentStrategy - strategy or null
     */
    public PaymentStrategy getStrategy(String paymentMethod) {
        return strategies.get(paymentMethod.toUpperCase());
    }
}
