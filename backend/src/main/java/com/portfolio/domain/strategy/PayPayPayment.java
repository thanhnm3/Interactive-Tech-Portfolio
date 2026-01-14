package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * PayPay payment strategy implementation
 * Processes payments via PayPay QR code payment system
 */
@Component
public class PayPayPayment implements PaymentStrategy {

    private static final String PAYMENT_METHOD = "PAYPAY";
    private static final double PROCESSING_FEE_RATE = 0.02; // 2%
    private static final BigDecimal MAX_AMOUNT = BigDecimal.valueOf(500000);

    /**
     * Process PayPay payment
     * @param order - order to process
     * @return PaymentResult - payment result
     */
    @Override
    public PaymentResult process(Order order) {
        if (!canProcess(order)) {
            return PaymentResult.failure(
                "AMOUNT_EXCEEDED",
                "Order amount exceeds PayPay limit",
                PAYMENT_METHOD
            );
        }

        Money orderAmount = order.getTotalAmount();
        Money processingFee = calculateProcessingFee(orderAmount);

        String transactionId = generateTransactionId();

        // PayPay typically returns pending status initially
        // Real implementation would redirect to PayPay app/QR
        return PaymentResult.pending(
            transactionId,
            orderAmount,
            PAYMENT_METHOD
        );
    }

    /**
     * Validate if order can be processed via PayPay
     * @param order - order to validate
     * @return boolean - true if valid
     */
    @Override
    public boolean canProcess(Order order) {
        if (order == null || order.getTotalAmount() == null) {
            return false;
        }

        BigDecimal amount = order.getTotalAmount().getAmount();
        return amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(MAX_AMOUNT) <= 0;
    }

    /**
     * Get payment method name
     * @return String - "PAYPAY"
     */
    @Override
    public String getPaymentMethodName() {
        return PAYMENT_METHOD;
    }

    /**
     * Get processing fee rate
     * @return double - 0.02 (2%)
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
     * Generate unique transaction ID for PayPay
     * @return String - transaction ID
     */
    private String generateTransactionId() {
        return "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
