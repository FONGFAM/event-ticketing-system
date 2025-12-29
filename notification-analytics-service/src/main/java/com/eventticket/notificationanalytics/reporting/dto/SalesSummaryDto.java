package com.eventticket.notificationanalytics.reporting.dto;

import java.util.Comparator;
import java.util.List;

public class SalesSummaryDto {
     private int totalEvents;
     private int totalTicketsSold;
     private double totalRevenue;
     private double averageTicketPrice;
     private String topEvent;
     private double weeklyRevenue;

     public int getTotalEvents() {
          return totalEvents;
     }

     public void setTotalEvents(int totalEvents) {
          this.totalEvents = totalEvents;
     }

     public int getTotalTicketsSold() {
          return totalTicketsSold;
     }

     public void setTotalTicketsSold(int totalTicketsSold) {
          this.totalTicketsSold = totalTicketsSold;
     }

     public double getTotalRevenue() {
          return totalRevenue;
     }

     public void setTotalRevenue(double totalRevenue) {
          this.totalRevenue = totalRevenue;
     }

     public double getAverageTicketPrice() {
          return averageTicketPrice;
     }

     public void setAverageTicketPrice(double averageTicketPrice) {
          this.averageTicketPrice = averageTicketPrice;
     }

     public String getTopEvent() {
          return topEvent;
     }

     public void setTopEvent(String topEvent) {
          this.topEvent = topEvent;
     }

     public double getWeeklyRevenue() {
          return weeklyRevenue;
     }

     public void setWeeklyRevenue(double weeklyRevenue) {
          this.weeklyRevenue = weeklyRevenue;
     }

     public static SalesSummaryDto fromReports(List<EventReportDto> reports) {
          SalesSummaryDto summary = new SalesSummaryDto();
          int totalEvents = reports.size();
          int totalTickets = reports.stream().mapToInt(EventReportDto::getSoldSeats).sum();
          double totalRevenue = reports.stream().mapToDouble(EventReportDto::getTotalRevenue).sum();

          summary.setTotalEvents(totalEvents);
          summary.setTotalTicketsSold(totalTickets);
          summary.setTotalRevenue(totalRevenue);
          summary.setAverageTicketPrice(totalTickets > 0 ? totalRevenue / totalTickets : 0);
          summary.setWeeklyRevenue(totalRevenue);

          summary.setTopEvent(reports.stream()
                    .max(Comparator.comparingDouble(EventReportDto::getTotalRevenue))
                    .map(EventReportDto::getEventName)
                    .orElse("N/A"));

          return summary;
     }
}
