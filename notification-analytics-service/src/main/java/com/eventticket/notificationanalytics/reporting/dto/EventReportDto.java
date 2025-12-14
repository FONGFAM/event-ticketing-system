package com.eventticket.notificationanalytics.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReportDto {
     private String eventId;
     private String eventName;
     private int totalSeats;
     private int soldSeats;
     private int availableSeats;
     private double totalRevenue;
     private double occupancyRate;

     // Lombok workaround: explicit getters and setters
     public String getEventId() {
           return eventId;
     }

     public void setEventId(String eventId) {
           this.eventId = eventId;
     }

     public String getEventName() {
           return eventName;
     }

     public void setEventName(String eventName) {
           this.eventName = eventName;
     }

     public int getTotalSeats() {
           return totalSeats;
     }

     public void setTotalSeats(int totalSeats) {
           this.totalSeats = totalSeats;
     }

     public int getSoldSeats() {
           return soldSeats;
     }

     public void setSoldSeats(int soldSeats) {
           this.soldSeats = soldSeats;
     }

     public int getAvailableSeats() {
           return availableSeats;
     }

     public void setAvailableSeats(int availableSeats) {
           this.availableSeats = availableSeats;
     }

     public double getTotalRevenue() {
           return totalRevenue;
     }

     public void setTotalRevenue(double totalRevenue) {
           this.totalRevenue = totalRevenue;
     }

     public double getOccupancyRate() {
           return occupancyRate;
     }

     public void setOccupancyRate(double occupancyRate) {
           this.occupancyRate = occupancyRate;
     }

     public static EventReportDtoBuilder builder() {
           return new EventReportDtoBuilder();
     }

     public static class EventReportDtoBuilder {
           private String eventId;
           private String eventName;
           private int totalSeats;
           private int soldSeats;
           private int availableSeats;
           private double totalRevenue;
           private double occupancyRate;

           public EventReportDtoBuilder eventId(String eventId) {
                 this.eventId = eventId;
                 return this;
           }

           public EventReportDtoBuilder eventName(String eventName) {
                 this.eventName = eventName;
                 return this;
           }

           public EventReportDtoBuilder totalSeats(int totalSeats) {
                 this.totalSeats = totalSeats;
                 return this;
           }

           public EventReportDtoBuilder soldSeats(int soldSeats) {
                 this.soldSeats = soldSeats;
                 return this;
           }

           public EventReportDtoBuilder availableSeats(int availableSeats) {
                 this.availableSeats = availableSeats;
                 return this;
           }

           public EventReportDtoBuilder totalRevenue(double totalRevenue) {
                 this.totalRevenue = totalRevenue;
                 return this;
           }

           public EventReportDtoBuilder occupancyRate(double occupancyRate) {
                 this.occupancyRate = occupancyRate;
                 return this;
           }

           public EventReportDto build() {
                 EventReportDto dto = new EventReportDto();
                 dto.eventId = this.eventId;
                 dto.eventName = this.eventName;
                 dto.totalSeats = this.totalSeats;
                 dto.soldSeats = this.soldSeats;
                 dto.availableSeats = this.availableSeats;
                 dto.totalRevenue = this.totalRevenue;
                 dto.occupancyRate = this.occupancyRate;
                 return dto;
           }
     }
}
