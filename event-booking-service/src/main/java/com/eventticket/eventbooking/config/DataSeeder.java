package com.eventticket.eventbooking.config;

import com.eventticket.eventbooking.entity.Event;
import com.eventticket.eventbooking.entity.Seat;
import com.eventticket.eventbooking.repository.EventRepository;
import com.eventticket.eventbooking.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
     private final EventRepository eventRepository;
     private final SeatRepository seatRepository;

     public DataSeeder(EventRepository eventRepository, SeatRepository seatRepository) {
          this.eventRepository = eventRepository;
          this.seatRepository = seatRepository;
     }

     @Override
     public void run(String... args) {
          double defaultPrice = 500000.0;

          if (eventRepository.count() > 0) {
               eventRepository.findAll().forEach(event -> {
                    Double price = event.getPrice();
                    if (price == null || price <= 0) {
                         event.setPrice(defaultPrice);
                         eventRepository.save(event);
                    }
               });
               return;
          }

          Event event = Event.builder()
                    .name("Live Concert")
                    .venueName("Central Hall")
                    .startTime(LocalDateTime.now().plusDays(7))
                    .endTime(LocalDateTime.now().plusDays(7).plusHours(2))
                    .description("Seeded event for local development")
                    .price(defaultPrice)
                    .totalSeats(3)
                    .availableSeats(3)
                    .soldSeats(0)
                    .build();

          Event savedEvent = eventRepository.save(event);

          List<Seat> seats = List.of(
                    Seat.builder()
                              .eventId(savedEvent.getId())
                              .row("A")
                              .col(1)
                              .status("AVAILABLE")
                              .build(),
                    Seat.builder()
                              .eventId(savedEvent.getId())
                              .row("A")
                              .col(2)
                              .status("AVAILABLE")
                              .build(),
                    Seat.builder()
                              .eventId(savedEvent.getId())
                              .row("A")
                              .col(3)
                              .status("AVAILABLE")
                              .build()
          );

          seatRepository.saveAll(seats);
     }
}
