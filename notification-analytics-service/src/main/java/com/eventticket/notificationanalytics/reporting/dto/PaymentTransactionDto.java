package com.eventticket.notificationanalytics.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentTransactionDto {
     private String id;
     private String userId;
     private String eventId;
     private double amount;
     private String status;
     private String paymentMethod;
     private String transactionId;
     private String createdAt;
     private String updatedAt;

     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getUserId() {
          return userId;
     }

     public void setUserId(String userId) {
          this.userId = userId;
     }

     public String getEventId() {
          return eventId;
     }

     public void setEventId(String eventId) {
          this.eventId = eventId;
     }

     public double getAmount() {
          return amount;
     }

     public void setAmount(double amount) {
          this.amount = amount;
     }

     public String getStatus() {
          return status;
     }

     public void setStatus(String status) {
          this.status = status;
     }

     public String getPaymentMethod() {
          return paymentMethod;
     }

     public void setPaymentMethod(String paymentMethod) {
          this.paymentMethod = paymentMethod;
     }

     public String getTransactionId() {
          return transactionId;
     }

     public void setTransactionId(String transactionId) {
          this.transactionId = transactionId;
     }

     public String getCreatedAt() {
          return createdAt;
     }

     public void setCreatedAt(String createdAt) {
          this.createdAt = createdAt;
     }

     public String getUpdatedAt() {
          return updatedAt;
     }

     public void setUpdatedAt(String updatedAt) {
          this.updatedAt = updatedAt;
     }
}
