package com.eventticket.eventbooking.seat.controller;

import com.eventticket.common.dto.ApiResponse;
import com.eventticket.common.dto.SeatDto;
import com.eventticket.eventbooking.seat.service.SeatAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatAllocationController {
      private final SeatAllocationService seatAllocationService;

      public SeatAllocationController(SeatAllocationService seatAllocationService) {
            this.seatAllocationService = seatAllocationService;
      }

      @PostMapping("/hold")
      public ResponseEntity<ApiResponse<SeatDto>> holdSeat(
                  @RequestParam("eventId") String eventId,
                  @RequestParam("seatId") String seatId,
                  @RequestParam("userId") String userId) {
            SeatDto seat = seatAllocationService.holdSeat(eventId, seatId, userId);
            return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(ApiResponse.ok(seat, "Seat held successfully for 5 minutes"));
      }

      @PostMapping("/reserve")
      public ResponseEntity<ApiResponse<List<SeatDto>>> reserveSeats(@RequestBody ReserveRequest request) {
            List<SeatDto> seats = seatAllocationService.reserveSeats(
                      request.getEventId(),
                      request.getUserId(),
                      request.getQuantity());
            return ResponseEntity
                      .status(HttpStatus.CREATED)
                      .body(ApiResponse.ok(seats, "Seats reserved successfully"));
      }

      @PostMapping("/release")
      public ResponseEntity<ApiResponse<Void>> releaseSeat(
                  @RequestParam("eventId") String eventId,
                  @RequestParam("seatId") String seatId,
                  @RequestParam("userId") String userId) {
            seatAllocationService.releaseSeat(eventId, seatId, userId);
            return ResponseEntity.ok(ApiResponse.ok(null, "Seat released successfully"));
      }

      @PostMapping("/confirm")
      public ResponseEntity<ApiResponse<Void>> confirmSeat(
                  @RequestParam("eventId") String eventId,
                  @RequestParam("seatId") String seatId,
                  @RequestParam("userId") String userId) {
            seatAllocationService.confirmSeat(eventId, seatId, userId);
            return ResponseEntity.ok(ApiResponse.ok(null, "Seat confirmed successfully"));
      }

      @GetMapping("/status")
      public ResponseEntity<ApiResponse<SeatDto>> getSeatStatus(
                  @RequestParam("eventId") String eventId,
                  @RequestParam("seatId") String seatId) {
            SeatDto seat = seatAllocationService.getSeatStatus(eventId, seatId);
            return ResponseEntity.ok(ApiResponse.ok(seat));
      }

      public static class ReserveRequest {
            private String eventId;
            private String userId;
            private int quantity;

            public String getEventId() {
                  return eventId;
            }

            public void setEventId(String eventId) {
                  this.eventId = eventId;
            }

            public String getUserId() {
                  return userId;
            }

            public void setUserId(String userId) {
                  this.userId = userId;
            }

            public int getQuantity() {
                  return quantity;
            }

            public void setQuantity(int quantity) {
                  this.quantity = quantity;
            }
      }
}
