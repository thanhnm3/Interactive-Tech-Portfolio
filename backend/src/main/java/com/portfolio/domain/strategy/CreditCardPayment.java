package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Credit card payment strategy implementation
 * Processes payments via credit card with standard fee rate
 */
@Component
public class CreditCardPayment implements PaymentStrategy {

    private static final String PAYMENT_METHOD = "CREDIT_CARD";
    private static final double PROCESSING_FEE_RATE = 0.03; // 3%
    private static final BigDecimal MIN_AMOUNT = BigDecimal.valueOf(100);
    private static final BigDecimal MAX_AMOUNT = BigDecimal.valueOf(1000000);

    /**
     * Process credit card payment
     * @param order - order to process
     * @return PaymentResult - payment result
     */
    @Override
    public PaymentResult process(Order order) {
        if (!canProcess(order)) {
            return PaymentResult.failure(
                "INVALID_AMOUNT",
                "Order amount is outside allowed range for credit card",
                PAYMENT_METHOD
            );
        }

        Money orderAmount = order.getTotalAmount();
        Money processingFee = calculateProcessingFee(orderAmount);

        // Simulate credit card processing
        String transactionId = generateTransactionId();

        // In real implementation, this would call payment gateway
        boolean isApproved = simulateCardAuthorization();

        if (isApproved) {
            return PaymentResult.success(
                transactionId,
                orderAmount,
                processingFee,
                PAYMENT_METHOD
            );
        } else {
            return PaymentResult.failure(
                "CARD_DECLINED",
                "Credit card was declined by issuer",
                PAYMENT_METHOD
            );
        }
    }

    /**
     * Validate if order can be processed via credit card
     * @param order - order to validate
     * @return boolean - true if valid
     */
    @Override
    public boolean canProcess(Order order) {
        if (order == null || order.getTotalAmount() == null) {
            return false;
        }

        BigDecimal amount = order.getTotalAmount().getAmount();
        return amount.compareTo(MIN_AMOUNT) >= 0 && amount.compareTo(MAX_AMOUNT) <= 0;
    }

    /**
     * Get payment method name
     * @return String - "CREDIT_CARD"
     */
    @Override
    public String getPaymentMethodName() {
        return PAYMENT_METHOD;
    }

    /**
     * Get processing fee rate
     * @return double - 0.03 (3%)
     */
    @Override
    public double getProcessingFeeRate() {
        return PROCESSING_FEE_RATE;
    }

    /**
     * Calculate processing fee
     * @param amount - order amount
     * @return Money - processing fee
     */
    private Money calculateProcessingFee(Money amount) {
        return amount.multiply(BigDecimal.valueOf(PROCESSING_FEE_RATE));
    }

    /**
     * Generate unique transaction ID
     * @return String - transaction ID
     */
    private String generateTransactionId() {
        return "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Simulate card authorization (demo purposes)
     * @return boolean - authorization result
     */
    private boolean simulateCardAuthorization() {
        // In production, this would call actual payment gateway
        return true;
    }
}
