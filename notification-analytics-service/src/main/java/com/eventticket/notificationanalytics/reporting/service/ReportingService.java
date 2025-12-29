package com.eventticket.notificationanalytics.reporting.service;

import com.eventticket.common.dto.ApiResponse;
import com.eventticket.common.dto.EventDto;
import com.eventticket.common.dto.TicketDto;
import com.eventticket.notificationanalytics.reporting.dto.EventReportDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class ReportingService {
     private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);
     private static final String EVENT_SERVICE_URL = "http://localhost:8001/api/events";
     private static final String TICKETING_SERVICE_URL = "http://localhost:8004/api/tickets";

     private final WebClient webClient;

     public ReportingService(WebClient webClient) {
          this.webClient = webClient;
     }

     public EventReportDto getEventReport(String eventId) {
          logger.info("Generating report for event: {}", eventId);
          EventDto event = fetchEvents()
                    .stream()
                    .filter(item -> eventId.equals(item.getId()))
                    .findFirst()
                    .orElse(null);

          if (event == null) {
               return EventReportDto.builder()
                         .eventId(eventId)
                         .eventName("Unknown Event")
                         .totalSeats(0)
                         .soldSeats(0)
                         .availableSeats(0)
                         .totalRevenue(0)
                         .occupancyRate(0)
                         .build();
          }

          int soldSeats = countTicketsByEvent(eventId);
          double totalRevenue = calculateTotalRevenue(event, soldSeats);
          int availableSeats = Math.max(0, event.getTotalSeats() - soldSeats);
          double occupancyRate = event.getTotalSeats() > 0
                    ? (soldSeats * 100.0 / event.getTotalSeats())
                    : 0;

          return EventReportDto.builder()
                    .eventId(event.getId())
                    .eventName(event.getName())
                    .totalSeats(event.getTotalSeats())
                    .soldSeats(soldSeats)
                    .availableSeats(availableSeats)
                    .totalRevenue(totalRevenue)
                    .occupancyRate(occupancyRate)
                    .build();
     }

     public EventReportDto[] getAllEventReports() {
          logger.info("Generating reports for all events");
          java.util.List<EventDto> events = fetchEvents();
          java.util.List<EventReportDto> reports = new java.util.ArrayList<>();

          for (EventDto event : events) {
               int soldSeats = countTicketsByEvent(event.getId());
               double totalRevenue = calculateTotalRevenue(event, soldSeats);
               int availableSeats = Math.max(0, event.getTotalSeats() - soldSeats);
               double occupancyRate = event.getTotalSeats() > 0
                         ? (soldSeats * 100.0 / event.getTotalSeats())
                         : 0;

               reports.add(EventReportDto.builder()
                         .eventId(event.getId())
                         .eventName(event.getName())
                         .totalSeats(event.getTotalSeats())
                         .soldSeats(soldSeats)
                         .availableSeats(availableSeats)
                         .totalRevenue(totalRevenue)
                         .occupancyRate(occupancyRate)
                         .build());
          }

          return reports.toArray(new EventReportDto[0]);
     }

     private java.util.List<EventDto> fetchEvents() {
          return fetchList(EVENT_SERVICE_URL, new ParameterizedTypeReference<ApiResponse<java.util.List<EventDto>>>() {});
     }

     private java.util.List<TicketDto> fetchTickets() {
          return fetchList(TICKETING_SERVICE_URL, new ParameterizedTypeReference<ApiResponse<java.util.List<TicketDto>>>() {});
     }

     private <T> java.util.List<T> fetchList(String url, ParameterizedTypeReference<ApiResponse<java.util.List<T>>> type) {
          try {
               ApiResponse<java.util.List<T>> response = webClient.get()
                         .uri(url)
                         .retrieve()
                         .bodyToMono(type)
                         .block();

               if (response != null && response.isSuccess() && response.getData() != null) {
                    return response.getData();
               }
          } catch (Exception ex) {
               logger.warn("Failed to fetch data from {}: {}", url, ex.getMessage());
          }
          return java.util.List.of();
     }

     private int countTicketsByEvent(String eventId) {
          return (int) fetchTickets().stream()
                    .filter(ticket -> eventId.equals(ticket.getEventId()))
                    .filter(ticket -> ticket.getStatus() == null || !"CANCELLED".equalsIgnoreCase(ticket.getStatus()))
                    .count();
     }

     private double calculateTotalRevenue(EventDto event, int soldSeats) {
          if (event == null || event.getPrice() <= 0 || soldSeats <= 0) {
               return 0;
          }
          return event.getPrice() * soldSeats;
     }
}
