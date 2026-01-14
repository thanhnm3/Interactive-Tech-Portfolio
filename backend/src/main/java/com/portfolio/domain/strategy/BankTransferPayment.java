package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Bank transfer payment strategy implementation
 * Processes payments via bank transfer with fixed fee
 */
@Component
public class BankTransferPayment implements PaymentStrategy {

    private static final String PAYMENT_METHOD = "BANK_TRANSFER";
    private static final double PROCESSING_FEE_RATE = 0.0; // No percentage fee
    private static final BigDecimal FIXED_FEE = BigDecimal.valueOf(330); // ¥330 fixed fee
    private static final BigDecimal MIN_AMOUNT = BigDecimal.valueOf(1000);

    /**
     * Process bank transfer payment
     * @param order - order to process
     * @return PaymentResult - payment result
     */
    @Override
    public PaymentResult process(Order order) {
        if (!canProcess(order)) {
            return PaymentResult.failure(
                "MIN_AMOUNT_NOT_MET",
                "Order amount is below minimum for bank transfer",
                PAYMENT_METHOD
            );
        }

        Money orderAmount = order.getTotalAmount();
        Money processingFee = Money.of(FIXED_FEE);

        String transactionId = generateTransactionId();

        // Bank transfers are always pending until confirmed
        return PaymentResult.pending(
            transactionId,
            orderAmount,
            PAYMENT_METHOD
        );
    }

    /**
     * Validate if order can be processed via bank transfer
     * @param order - order to validate
     * @return boolean - true if valid
     */
    @Override
    public boolean canProcess(Order order) {
        if (order == null || order.getTotalAmount() == null) {
            return false;
        }

        BigDecimal amount = order.getTotalAmount().getAmount();
        return amount.compareTo(MIN_AMOUNT) >= 0;
    }

    /**
     * Get payment method name
     * @return String - "BANK_TRANSFER"
     */
    @Override
    public String getPaymentMethodName() {
        return PAYMENT_METHOD;
    }

    /**
     * Get processing fee rate (0 for bank transfer, uses fixed fee)
     * @return double - 0.0
     */
    @Override
    public double getProcessingFeeRate() {
        return PROCESSING_FEE_RATE;
    }

    /**
     * Get fixed transfer fee
     * @return Money - fixed fee amount
     */
    public Money getFixedFee() {
        return Money.of(FIXED_FEE);
    }

    /**
     * Generate unique transaction ID for bank transfer
     * @return String - transaction ID
     */
    private String generateTransactionId() {
        return "BT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generate bank transfer instructions
     * @param order - order to generate instructions for
     * @return String - transfer instructions
     */
    public String generateTransferInstructions(Order order) {
        return String.format("""
            Bank Transfer Instructions:
            
            Bank: みずほ銀行 (Mizuho Bank)
            Branch: 東京営業部 (Tokyo Branch)
            Account Type: 普通 (Savings)
            Account Number: 1234567
            Account Name: カブシキカイシャポートフォリオ
            
            Amount: %s
            Reference: %s
            
            Please complete transfer within 3 business days.
            """,
            order.getTotalAmount().toString(),
            order.getOrderNumber()
        );
    }
}
