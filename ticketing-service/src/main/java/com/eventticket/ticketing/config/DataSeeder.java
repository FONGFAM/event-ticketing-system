package com.eventticket.ticketing.config;

import com.eventticket.ticketing.entity.Ticket;
import com.eventticket.ticketing.repository.TicketRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
     private final TicketRepository ticketRepository;

     public DataSeeder(TicketRepository ticketRepository) {
          this.ticketRepository = ticketRepository;
     }

     @Override
     public void run(String... args) {
          if (ticketRepository.count() > 0) {
               return;
          }

          List<Ticket> tickets = List.of(
                    Ticket.builder()
                              .eventId("event-1001")
                              .seatId("seat-A1001")
                              .userId("user-1001")
                              .paymentId("payment-1001")
                              .qrCode("QR-DEMO-1001")
                              .status("ACTIVE")
                              .build(),
                    Ticket.builder()
                              .eventId("event-1002")
                              .seatId("seat-B1002")
                              .userId("user-1002")
                              .paymentId("payment-1002")
                              .qrCode("QR-DEMO-1002")
                              .status("ACTIVE")
                              .build()
          );

          ticketRepository.saveAll(tickets);
     }
}
