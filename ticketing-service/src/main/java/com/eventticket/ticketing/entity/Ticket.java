package com.eventticket.ticketing.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     private String id;

     @Column(nullable = false)
     private String eventId;

     @Column(nullable = false)
     private String seatId;

     @Column(nullable = false)
     private String userId;

     @Column(nullable = false)
     private String paymentId;

     @Column(nullable = false)
     private String qrCode;

     @Column(columnDefinition = "BYTEA")
     private byte[] qrCodeImage;

     @Column(nullable = false)
     private String status; // ACTIVE, USED, CANCELLED

     @Column(nullable = false)
     private LocalDateTime createdAt;

     @Column(nullable = false)
     private LocalDateTime updatedAt;

     @Column
     private LocalDateTime checkedInAt;

     @PrePersist
     protected void onCreate() {
          createdAt = LocalDateTime.now();
          updatedAt = LocalDateTime.now();
     }

     @PreUpdate
     protected void onUpdate() {
          updatedAt = LocalDateTime.now();
     }

     // Lombok workaround: explicit getters and setters
     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getEventId() {
          return eventId;
     }

     public void setEventId(String eventId) {
          this.eventId = eventId;
     }

     public String getSeatId() {
          return seatId;
     }

     public void setSeatId(String seatId) {
          this.seatId = seatId;
     }

     public String getUserId() {
          return userId;
     }

     public void setUserId(String userId) {
          this.userId = userId;
     }

     public String getPaymentId() {
          return paymentId;
     }

     public void setPaymentId(String paymentId) {
          this.paymentId = paymentId;
     }

     public String getQrCode() {
          return qrCode;
     }

     public void setQrCode(String qrCode) {
          this.qrCode = qrCode;
     }

     public byte[] getQrCodeImage() {
          return qrCodeImage;
     }

     public void setQrCodeImage(byte[] qrCodeImage) {
          this.qrCodeImage = qrCodeImage;
     }

     public String getStatus() {
          return status;
     }

     public void setStatus(String status) {
          this.status = status;
     }

     public LocalDateTime getCreatedAt() {
          return createdAt;
     }

     public void setCreatedAt(LocalDateTime createdAt) {
          this.createdAt = createdAt;
     }

     public LocalDateTime getUpdatedAt() {
          return updatedAt;
     }

     public void setUpdatedAt(LocalDateTime updatedAt) {
          this.updatedAt = updatedAt;
     }

     public LocalDateTime getCheckedInAt() {
          return checkedInAt;
     }

     public void setCheckedInAt(LocalDateTime checkedInAt) {
          this.checkedInAt = checkedInAt;
     }

     public static TicketBuilder builder() {
          return new TicketBuilder();
     }

     public static class TicketBuilder {
          private String id;
          private String eventId;
          private String seatId;
          private String userId;
          private String paymentId;
          private String qrCode;
          private byte[] qrCodeImage;
          private String status;
          private LocalDateTime createdAt;
          private LocalDateTime updatedAt;
          private LocalDateTime checkedInAt;

          public TicketBuilder id(String id) {
               this.id = id;
               return this;
          }

          public TicketBuilder eventId(String eventId) {
               this.eventId = eventId;
               return this;
          }

          public TicketBuilder seatId(String seatId) {
               this.seatId = seatId;
               return this;
          }

          public TicketBuilder userId(String userId) {
               this.userId = userId;
               return this;
          }

          public TicketBuilder paymentId(String paymentId) {
               this.paymentId = paymentId;
               return this;
          }

          public TicketBuilder qrCode(String qrCode) {
               this.qrCode = qrCode;
               return this;
          }

          public TicketBuilder qrCodeImage(byte[] qrCodeImage) {
               this.qrCodeImage = qrCodeImage;
               return this;
          }

          public TicketBuilder status(String status) {
               this.status = status;
               return this;
          }

          public TicketBuilder createdAt(LocalDateTime createdAt) {
               this.createdAt = createdAt;
               return this;
          }

          public TicketBuilder updatedAt(LocalDateTime updatedAt) {
               this.updatedAt = updatedAt;
               return this;
          }

          public TicketBuilder checkedInAt(LocalDateTime checkedInAt) {
               this.checkedInAt = checkedInAt;
               return this;
          }

          public Ticket build() {
               Ticket ticket = new Ticket();
               ticket.id = this.id;
               ticket.eventId = this.eventId;
               ticket.seatId = this.seatId;
               ticket.userId = this.userId;
               ticket.paymentId = this.paymentId;
               ticket.qrCode = this.qrCode;
               ticket.qrCodeImage = this.qrCodeImage;
               ticket.status = this.status;
               ticket.createdAt = this.createdAt;
               ticket.updatedAt = this.updatedAt;
               ticket.checkedInAt = this.checkedInAt;
               return ticket;
          }
     }
}
