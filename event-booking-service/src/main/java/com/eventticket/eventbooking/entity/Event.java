package com.eventticket.eventbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     private String id;

     @Column(nullable = false)
     private String name;

     @Column(nullable = false)
     private String venueName;

     @Column(nullable = false)
     private LocalDateTime startTime;

     @Column(nullable = false)
     private LocalDateTime endTime;

     @Column(columnDefinition = "TEXT")
     private String description;

     @Column(nullable = false)
     private int totalSeats;

     @Column(nullable = false)
     private int availableSeats;

     @Column(nullable = false)
     private int soldSeats;

     @Column(nullable = false)
     private LocalDateTime createdAt;

     @Column(nullable = false)
     private LocalDateTime updatedAt;

     public Event() {}

     public Event(String id, String name, String venueName, LocalDateTime startTime, LocalDateTime endTime,
                  String description, int totalSeats, int availableSeats, int soldSeats,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
          this.id = id;
          this.name = name;
          this.venueName = venueName;
          this.startTime = startTime;
          this.endTime = endTime;
          this.description = description;
          this.totalSeats = totalSeats;
          this.availableSeats = availableSeats;
          this.soldSeats = soldSeats;
          this.createdAt = createdAt;
          this.updatedAt = updatedAt;
     }

     public String getId() { return id; }
     public void setId(String id) { this.id = id; }

     public String getName() { return name; }
     public void setName(String name) { this.name = name; }

     public String getVenueName() { return venueName; }
     public void setVenueName(String venueName) { this.venueName = venueName; }

     public LocalDateTime getStartTime() { return startTime; }
     public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

     public LocalDateTime getEndTime() { return endTime; }
     public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

     public String getDescription() { return description; }
     public void setDescription(String description) { this.description = description; }

     public int getTotalSeats() { return totalSeats; }
     public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

     public int getAvailableSeats() { return availableSeats; }
     public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

     public int getSoldSeats() { return soldSeats; }
     public void setSoldSeats(int soldSeats) { this.soldSeats = soldSeats; }

     public LocalDateTime getCreatedAt() { return createdAt; }
     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

     public LocalDateTime getUpdatedAt() { return updatedAt; }
     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

     public static EventBuilder builder() {
          return new EventBuilder();
     }

     public static class EventBuilder {
          private String id;
          private String name;
          private String venueName;
          private LocalDateTime startTime;
          private LocalDateTime endTime;
          private String description;
          private int totalSeats;
          private int availableSeats;
          private int soldSeats;
          private LocalDateTime createdAt;
          private LocalDateTime updatedAt;

          public EventBuilder id(String id) { this.id = id; return this; }
          public EventBuilder name(String name) { this.name = name; return this; }
          public EventBuilder venueName(String venueName) { this.venueName = venueName; return this; }
          public EventBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
          public EventBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
          public EventBuilder description(String description) { this.description = description; return this; }
          public EventBuilder totalSeats(int totalSeats) { this.totalSeats = totalSeats; return this; }
          public EventBuilder availableSeats(int availableSeats) { this.availableSeats = availableSeats; return this; }
          public EventBuilder soldSeats(int soldSeats) { this.soldSeats = soldSeats; return this; }
          public EventBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
          public EventBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

          public Event build() {
               return new Event(id, name, venueName, startTime, endTime, description, totalSeats, availableSeats, soldSeats, createdAt, updatedAt);
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
