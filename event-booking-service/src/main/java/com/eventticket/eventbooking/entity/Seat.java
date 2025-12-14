package com.eventticket.eventbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "row", "col" }))
public class Seat {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     private String id;

     @Column(name = "event_id", nullable = false)
     private String eventId;

     @Column(nullable = false)
     private String row;

     @Column(nullable = false)
     private int col;

     @Column(nullable = false)
     private String status; // AVAILABLE, BLOCKED, SOLD

     @Column(columnDefinition = "TEXT")
     private String heldBy;

     @Column
     private Long heldUntil;

     @Column(nullable = false)
     private LocalDateTime createdAt;

     @Column(nullable = false)
     private LocalDateTime updatedAt;

     public Seat() {}

     public Seat(String id, String eventId, String row, int col, String status, String heldBy, Long heldUntil,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
          this.id = id;
          this.eventId = eventId;
          this.row = row;
          this.col = col;
          this.status = status;
          this.heldBy = heldBy;
          this.heldUntil = heldUntil;
          this.createdAt = createdAt;
          this.updatedAt = updatedAt;
     }

     public String getId() { return id; }
     public void setId(String id) { this.id = id; }

     public String getEventId() { return eventId; }
     public void setEventId(String eventId) { this.eventId = eventId; }

     public String getRow() { return row; }
     public void setRow(String row) { this.row = row; }

     public int getCol() { return col; }
     public void setCol(int col) { this.col = col; }

     public String getStatus() { return status; }
     public void setStatus(String status) { this.status = status; }

     public String getHeldBy() { return heldBy; }
     public void setHeldBy(String heldBy) { this.heldBy = heldBy; }

     public Long getHeldUntil() { return heldUntil; }
     public void setHeldUntil(Long heldUntil) { this.heldUntil = heldUntil; }

     public LocalDateTime getCreatedAt() { return createdAt; }
     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

     public LocalDateTime getUpdatedAt() { return updatedAt; }
     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

     public static SeatBuilder builder() {
          return new SeatBuilder();
     }

     public static class SeatBuilder {
          private String id;
          private String eventId;
          private String row;
          private int col;
          private String status;
          private String heldBy;
          private Long heldUntil;
          private LocalDateTime createdAt;
          private LocalDateTime updatedAt;

          public SeatBuilder id(String id) { this.id = id; return this; }
          public SeatBuilder eventId(String eventId) { this.eventId = eventId; return this; }
          public SeatBuilder row(String row) { this.row = row; return this; }
          public SeatBuilder col(int col) { this.col = col; return this; }
          public SeatBuilder status(String status) { this.status = status; return this; }
          public SeatBuilder heldBy(String heldBy) { this.heldBy = heldBy; return this; }
          public SeatBuilder heldUntil(Long heldUntil) { this.heldUntil = heldUntil; return this; }
          public SeatBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
          public SeatBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

          public Seat build() {
               return new Seat(id, eventId, row, col, status, heldBy, heldUntil, createdAt, updatedAt);
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
