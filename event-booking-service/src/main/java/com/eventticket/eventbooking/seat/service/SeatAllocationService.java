package com.eventticket.eventbooking.seat.service;

import com.eventticket.common.dto.SeatDto;
import com.eventticket.common.exception.SeatAlreadyHeldException;
import com.eventticket.eventbooking.entity.Seat;
import com.eventticket.eventbooking.repository.EventRepository;
import com.eventticket.eventbooking.repository.SeatRepository;
import com.eventticket.eventbooking.seat.entity.SeatReservation;
import com.eventticket.eventbooking.seat.repository.SeatReservationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
public class SeatAllocationService {
     private static final Logger logger = LoggerFactory.getLogger(SeatAllocationService.class);
     private final SeatReservationRepository reservationRepository;
     private final SeatRepository seatRepository;
     private final EventRepository eventRepository;
     private final RedisTemplate<String, String> redisTemplate;

     public SeatAllocationService(SeatReservationRepository reservationRepository,
                                  SeatRepository seatRepository,
                                  EventRepository eventRepository,
                                  RedisTemplate<String, String> redisTemplate) {
          this.reservationRepository = reservationRepository;
          this.seatRepository = seatRepository;
          this.eventRepository = eventRepository;
          this.redisTemplate = redisTemplate;
     }

     private static final long HOLD_DURATION_MINUTES = 5;
     private static final String SEAT_LOCK_PREFIX = "seat_lock:";

     /**
      * Hold seat với Redis distributed lock
      * Redis command: SET key value NX PX 300000
      * Nếu key đã tồn tại, operation sẽ fail
      */
     public SeatDto holdSeat(String eventId, String seatId, String userId) {
          logger.info("Attempting to hold seat: eventId={}, seatId={}, userId={}", eventId, seatId, userId);

          Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found: " + seatId));

          if (!seat.getEventId().equals(eventId) || !"AVAILABLE".equals(seat.getStatus())) {
               throw new SeatAlreadyHeldException(seatId);
          }

          String lockKey = SEAT_LOCK_PREFIX + eventId + ":" + seatId;

          // Try to acquire lock with Redis NX (only set if not exists)
          Boolean lockAcquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, userId, Duration.ofMinutes(HOLD_DURATION_MINUTES));

          if (Boolean.FALSE.equals(lockAcquired)) {
               String currentHolder = redisTemplate.opsForValue().get(lockKey);
               logger.warn("Seat already held by: {}", currentHolder);
               throw new SeatAlreadyHeldException(seatId);
          }

          // Save reservation to database
          LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
          SeatReservation reservation = SeatReservation.builder()
                    .eventId(eventId)
                    .seatId(seatId)
                    .userId(userId)
                    .status("HELD")
                    .expiresAt(expiresAt)
                    .build();

          SeatReservation saved = reservationRepository.save(reservation);
          seat.setStatus("BLOCKED");
          seat.setHeldBy(userId);
          seat.setHeldUntil(expiresAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
          seatRepository.save(seat);
          logger.info("Seat held successfully: reservationId={}", saved.getId());

          return SeatDto.builder()
                    .id(seatId)
                    .eventId(eventId)
                    .status("BLOCKED")
                    .heldBy(userId)
                    .heldUntil(expiresAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();
     }

     /**
      * Release seat (người dùng hủy hoặc hết thời gian)
      */
     public void releaseSeat(String eventId, String seatId, String userId) {
          logger.info("Releasing seat: eventId={}, seatId={}, userId={}", eventId, seatId, userId);

          String lockKey = SEAT_LOCK_PREFIX + eventId + ":" + seatId;

          // Delete from Redis
          redisTemplate.delete(lockKey);

          // Update reservation status in database
          reservationRepository.findByEventIdAndSeatIdAndStatus(eventId, seatId, "HELD")
                    .ifPresent(reservation -> {
                         reservation.setStatus("RELEASED");
                         reservationRepository.save(reservation);
                    });

          seatRepository.findById(seatId).ifPresent(seat -> {
               if (seat.getEventId().equals(eventId)) {
                    if ("BLOCKED".equals(seat.getStatus())) {
                         seat.setStatus("AVAILABLE");
                         seat.setHeldBy(null);
                         seat.setHeldUntil(null);
                         seatRepository.save(seat);
                    }
               }
          });

          logger.info("Seat released: eventId={}, seatId={}", eventId, seatId);
     }

     /**
      * Confirm seat ownership (sau khi thanh toán thành công)
      */
     public void confirmSeat(String eventId, String seatId, String userId) {
          logger.info("Confirming seat: eventId={}, seatId={}, userId={}", eventId, seatId, userId);

          SeatReservation reservation = reservationRepository
                    .findByEventIdAndSeatIdAndStatus(eventId, seatId, "HELD")
                    .orElseThrow(() -> new RuntimeException("Reservation not found for seat: " + seatId));

          if (!reservation.getUserId().equals(userId)) {
               throw new RuntimeException("User does not own this reservation");
          }

          reservation.setStatus("CONFIRMED");
          reservation.setConfirmedAt(LocalDateTime.now());
          reservationRepository.save(reservation);

          seatRepository.findById(seatId).ifPresent(seat -> {
               if (seat.getEventId().equals(eventId)) {
                    seat.setStatus("SOLD");
                    seat.setHeldBy(null);
                    seat.setHeldUntil(null);
                    seatRepository.save(seat);
               }
          });

          eventRepository.findById(eventId).ifPresent(event -> {
               event.setAvailableSeats(Math.max(0, event.getAvailableSeats() - 1));
               event.setSoldSeats(event.getSoldSeats() + 1);
               eventRepository.save(event);
          });

          logger.info("Seat confirmed: eventId={}, seatId={}", eventId, seatId);
     }

     /**
      * Get current seat status
      */
     public SeatDto getSeatStatus(String eventId, String seatId) {
          Seat seat = seatRepository.findById(seatId).orElse(null);
          if (seat != null && !seat.getEventId().equals(eventId)) {
               return SeatDto.builder()
                         .id(seatId)
                         .eventId(eventId)
                         .status("AVAILABLE")
                         .build();
          }

          if (seat != null && "SOLD".equals(seat.getStatus())) {
               return SeatDto.builder()
                         .id(seatId)
                         .eventId(eventId)
                         .status("SOLD")
                         .build();
          }

          String lockKey = SEAT_LOCK_PREFIX + eventId + ":" + seatId;
          String heldBy = redisTemplate.opsForValue().get(lockKey);

          if (heldBy != null) {
               Long ttl = redisTemplate.getExpire(lockKey);
               long heldUntil = System.currentTimeMillis() + (ttl != null ? ttl * 1000 : 0);

               return SeatDto.builder()
                         .id(seatId)
                         .eventId(eventId)
                         .status("BLOCKED")
                         .heldBy(heldBy)
                         .heldUntil(heldUntil)
                         .build();
          }

          if (seat != null && "BLOCKED".equals(seat.getStatus())) {
               return SeatDto.builder()
                         .id(seatId)
                         .eventId(eventId)
                         .status("BLOCKED")
                         .heldBy(seat.getHeldBy())
                         .heldUntil(seat.getHeldUntil() != null ? seat.getHeldUntil() : 0L)
                         .build();
          }

          return SeatDto.builder()
                    .id(seatId)
                    .eventId(eventId)
                    .status("AVAILABLE")
                    .build();
     }

     /**
      * Clean up expired reservations (background job)
      */
     public void cleanupExpiredReservations() {
          logger.info("Cleaning up expired reservations...");
          LocalDateTime now = LocalDateTime.now();
          var expiredReservations = reservationRepository.findByStatusAndExpiresAtBefore("HELD", now);

          for (SeatReservation reservation : expiredReservations) {
               releaseSeat(reservation.getEventId(), reservation.getSeatId(), reservation.getUserId());
          }

          logger.info("Cleanup completed. Expired reservations: {}", expiredReservations.size());
     }

     public List<SeatDto> reserveSeats(String eventId, String userId, int quantity) {
          if (quantity <= 0) {
               throw new RuntimeException("Quantity must be greater than zero");
          }

          releaseExpiredHolds(eventId);

          List<Seat> availableSeats = seatRepository.findByEventIdAndStatusOrderByRowAscColAsc(
                    eventId,
                    "AVAILABLE",
                    PageRequest.of(0, quantity));

          if (availableSeats.size() < quantity) {
               throw new RuntimeException("Not enough available seats");
          }

          List<SeatDto> reserved = new ArrayList<>();
          try {
               for (Seat seat : availableSeats) {
                    reserved.add(holdSeat(eventId, seat.getId(), userId));
               }
          } catch (RuntimeException ex) {
               for (SeatDto seat : reserved) {
                    releaseSeat(eventId, seat.getId(), userId);
               }
               throw ex;
          }

          return reserved;
     }

     private void releaseExpiredHolds(String eventId) {
          long now = System.currentTimeMillis();
          List<Seat> blockedSeats = seatRepository.findByEventIdAndStatus(eventId, "BLOCKED");

          for (Seat seat : blockedSeats) {
               Long heldUntil = seat.getHeldUntil();
               if (heldUntil != null && heldUntil < now) {
                    releaseSeat(eventId, seat.getId(), seat.getHeldBy() != null ? seat.getHeldBy() : "system");
               }
          }
     }
}
