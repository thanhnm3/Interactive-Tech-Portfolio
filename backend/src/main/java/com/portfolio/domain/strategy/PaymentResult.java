package com.portfolio.domain.strategy;

import com.portfolio.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Result of payment processing
 * Contains transaction details and status
 */
public class PaymentResult {

    private final String transactionId;
    private final boolean isSuccess;
    private final String status;
    private final String message;
    private final Money amount;
    private final Money processingFee;
    private final String paymentMethod;
    private final LocalDateTime processedAt;
    private final String errorCode;

    /**
     * Private constructor for builder
     * @param builder - PaymentResult.Builder
     */
    private PaymentResult(Builder builder) {
        this.transactionId = builder.transactionId;
        this.isSuccess = builder.isSuccess;
        this.status = builder.status;
        this.message = builder.message;
        this.amount = builder.amount;
        this.processingFee = builder.processingFee;
        this.paymentMethod = builder.paymentMethod;
        this.processedAt = LocalDateTime.now();
        this.errorCode = builder.errorCode;
    }

    /**
     * Create success result
     * @param transactionId - transaction ID
     * @param amount - payment amount
     * @param processingFee - processing fee
     * @param paymentMethod - payment method used
     * @return PaymentResult - success result
     */
    public static PaymentResult success(String transactionId, Money amount,
                                        Money processingFee, String paymentMethod) {
        return new Builder()
            .transactionId(transactionId)
            .isSuccess(true)
            .status("COMPLETED")
            .message("Payment processed successfully")
            .amount(amount)
            .processingFee(processingFee)
            .paymentMethod(paymentMethod)
            .build();
    }

    /**
     * Create failure result
     * @param errorCode - error code
     * @param message - error message
     * @param paymentMethod - payment method attempted
     * @return PaymentResult - failure result
     */
    public static PaymentResult failure(String errorCode, String message, String paymentMethod) {
        return new Builder()
            .transactionId(null)
            .isSuccess(false)
            .status("FAILED")
            .message(message)
            .errorCode(errorCode)
            .paymentMethod(paymentMethod)
            .build();
    }

    /**
     * Create pending result
     * @param transactionId - transaction ID
     * @param amount - payment amount
     * @param paymentMethod - payment method
     * @return PaymentResult - pending result
     */
    public static PaymentResult pending(String transactionId, Money amount, String paymentMethod) {
        return new Builder()
            .transactionId(transactionId)
            .isSuccess(false)
            .status("PENDING")
            .message("Payment is pending confirmation")
            .amount(amount)
            .paymentMethod(paymentMethod)
            .build();
    }

    /**
     * Get transaction ID
     * @return String - transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Check if payment was successful
     * @return boolean - success status
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Get payment status
     * @return String - status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Get message
     * @return String - message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get payment amount
     * @return Money - amount
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Get processing fee
     * @return Money - fee
     */
    public Money getProcessingFee() {
        return processingFee;
    }

    /**
     * Get payment method
     * @return String - payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Get processed timestamp
     * @return LocalDateTime - processed at
     */
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    /**
     * Get error code
     * @return String - error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Builder for PaymentResult
     */
    public static class Builder {
        private String transactionId;
        private boolean isSuccess;
        private String status;
        private String message;
        private Money amount;
        private Money processingFee;
        private String paymentMethod;
        private String errorCode;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder isSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder processingFee(Money processingFee) {
            this.processingFee = processingFee;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public PaymentResult build() {
            return new PaymentResult(this);
        }
    }
}
