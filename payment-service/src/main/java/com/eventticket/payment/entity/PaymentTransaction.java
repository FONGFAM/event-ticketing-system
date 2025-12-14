package com.eventticket.payment.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class PaymentTransaction {
      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      private String id;

      @Column(nullable = false)
      private String userId;

      @Column(nullable = false)
      private String eventId;

      @Column(nullable = false)
      private double amount;

      @Column(nullable = false)
      private String status; // PENDING, CONFIRMED, FAILED, CANCELLED

      @Column(nullable = false)
      private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.

      @Column
      private String transactionId; // From payment gateway

      @Column(nullable = false)
      private LocalDateTime createdAt;

      @Column(nullable = false)
      private LocalDateTime updatedAt;

      public PaymentTransaction() {}

      public PaymentTransaction(String id, String userId, String eventId, double amount, String status,
                               String paymentMethod, String transactionId, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.userId = userId;
            this.eventId = eventId;
            this.amount = amount;
            this.status = status;
            this.paymentMethod = paymentMethod;
            this.transactionId = transactionId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
      }

      public String getId() { return id; }
      public void setId(String id) { this.id = id; }

      public String getUserId() { return userId; }
      public void setUserId(String userId) { this.userId = userId; }

      public String getEventId() { return eventId; }
      public void setEventId(String eventId) { this.eventId = eventId; }

      public double getAmount() { return amount; }
      public void setAmount(double amount) { this.amount = amount; }

      public String getStatus() { return status; }
      public void setStatus(String status) { this.status = status; }

      public String getPaymentMethod() { return paymentMethod; }
      public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

      public String getTransactionId() { return transactionId; }
      public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

      public LocalDateTime getCreatedAt() { return createdAt; }
      public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

      public LocalDateTime getUpdatedAt() { return updatedAt; }
      public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

      public static PaymentTransactionBuilder builder() {
            return new PaymentTransactionBuilder();
      }

      public static class PaymentTransactionBuilder {
            private String id;
            private String userId;
            private String eventId;
            private double amount;
            private String status;
            private String paymentMethod;
            private String transactionId;
            private LocalDateTime createdAt;
            private LocalDateTime updatedAt;

            public PaymentTransactionBuilder id(String id) { this.id = id; return this; }
            public PaymentTransactionBuilder userId(String userId) { this.userId = userId; return this; }
            public PaymentTransactionBuilder eventId(String eventId) { this.eventId = eventId; return this; }
            public PaymentTransactionBuilder amount(double amount) { this.amount = amount; return this; }
            public PaymentTransactionBuilder status(String status) { this.status = status; return this; }
            public PaymentTransactionBuilder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
            public PaymentTransactionBuilder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
            public PaymentTransactionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public PaymentTransactionBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

            public PaymentTransaction build() {
                  return new PaymentTransaction(id, userId, eventId, amount, status, paymentMethod, transactionId, createdAt, updatedAt);
            }
      }

      @PrePersist
      protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
      }

      @PreUpdate
      protected void onUpdate() {
            updatedAt = LocalDateTime.now();
      }
}
