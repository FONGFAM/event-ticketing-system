package com.eventticket.eventbooking.seat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatReservation {
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
      private String status; // HELD, CONFIRMED, RELEASED

      @Column(nullable = false)
      private LocalDateTime heldAt;

      @Column(nullable = false)
      private LocalDateTime expiresAt;

      @Column
      private LocalDateTime confirmedAt;

      @PrePersist
      protected void onCreate() {
            heldAt = LocalDateTime.now();
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

      public String getStatus() {
            return status;
      }

      public void setStatus(String status) {
            this.status = status;
      }

      public LocalDateTime getHeldAt() {
            return heldAt;
      }

      public void setHeldAt(LocalDateTime heldAt) {
            this.heldAt = heldAt;
      }

      public LocalDateTime getExpiresAt() {
            return expiresAt;
      }

      public void setExpiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
      }

      public LocalDateTime getConfirmedAt() {
            return confirmedAt;
      }

      public void setConfirmedAt(LocalDateTime confirmedAt) {
            this.confirmedAt = confirmedAt;
      }

      public static SeatReservationBuilder builder() {
            return new SeatReservationBuilder();
      }

      public static class SeatReservationBuilder {
            private String id;
            private String eventId;
            private String seatId;
            private String userId;
            private String status;
            private LocalDateTime heldAt;
            private LocalDateTime expiresAt;
            private LocalDateTime confirmedAt;

            public SeatReservationBuilder id(String id) {
                  this.id = id;
                  return this;
            }

            public SeatReservationBuilder eventId(String eventId) {
                  this.eventId = eventId;
                  return this;
            }

            public SeatReservationBuilder seatId(String seatId) {
                  this.seatId = seatId;
                  return this;
            }

            public SeatReservationBuilder userId(String userId) {
                  this.userId = userId;
                  return this;
            }

            public SeatReservationBuilder status(String status) {
                  this.status = status;
                  return this;
            }

            public SeatReservationBuilder heldAt(LocalDateTime heldAt) {
                  this.heldAt = heldAt;
                  return this;
            }

            public SeatReservationBuilder expiresAt(LocalDateTime expiresAt) {
                  this.expiresAt = expiresAt;
                  return this;
            }

            public SeatReservationBuilder confirmedAt(LocalDateTime confirmedAt) {
                  this.confirmedAt = confirmedAt;
                  return this;
            }

            public SeatReservation build() {
                  SeatReservation reservation = new SeatReservation();
                  reservation.id = this.id;
                  reservation.eventId = this.eventId;
                  reservation.seatId = this.seatId;
                  reservation.userId = this.userId;
                  reservation.status = this.status;
                  reservation.heldAt = this.heldAt;
                  reservation.expiresAt = this.expiresAt;
                  reservation.confirmedAt = this.confirmedAt;
                  return reservation;
            }
      }
}
