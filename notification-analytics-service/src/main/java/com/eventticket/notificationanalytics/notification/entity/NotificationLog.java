package com.eventticket.notificationanalytics.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     private String id;

     @Column(nullable = false)
     private String eventType;

     @Column(nullable = false, columnDefinition = "TEXT")
     private String payload;

     @Column(nullable = false)
     private String status;

     @Column(nullable = false)
     private LocalDateTime createdAt;

     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getEventType() {
          return eventType;
     }

     public void setEventType(String eventType) {
          this.eventType = eventType;
     }

     public String getPayload() {
          return payload;
     }

     public void setPayload(String payload) {
          this.payload = payload;
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

     @PrePersist
     protected void onCreate() {
          createdAt = LocalDateTime.now();
     }
}
