package com.eventticket.common.dto;

public class EventDto {
     private String id;
     private String name;
     private String venueName;
     private long startTime;
     private long endTime;
     private String description;
     private double price;
     private int totalSeats;
     private int availableSeats;
     private int soldSeats;

     public EventDto() {}

     public EventDto(String id, String name, String venueName, long startTime, long endTime,
                     String description, double price, int totalSeats, int availableSeats, int soldSeats) {
          this.id = id;
          this.name = name;
          this.venueName = venueName;
          this.startTime = startTime;
          this.endTime = endTime;
          this.description = description;
          this.price = price;
          this.totalSeats = totalSeats;
          this.availableSeats = availableSeats;
          this.soldSeats = soldSeats;
     }

     public String getId() { return id; }
     public void setId(String id) { this.id = id; }

     public String getName() { return name; }
     public void setName(String name) { this.name = name; }

     public String getVenueName() { return venueName; }
     public void setVenueName(String venueName) { this.venueName = venueName; }

     public long getStartTime() { return startTime; }
     public void setStartTime(long startTime) { this.startTime = startTime; }

     public long getEndTime() { return endTime; }
     public void setEndTime(long endTime) { this.endTime = endTime; }

     public String getDescription() { return description; }
     public void setDescription(String description) { this.description = description; }

     public double getPrice() { return price; }
     public void setPrice(double price) { this.price = price; }

     public int getTotalSeats() { return totalSeats; }
     public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

     public int getAvailableSeats() { return availableSeats; }
     public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

     public int getSoldSeats() { return soldSeats; }
     public void setSoldSeats(int soldSeats) { this.soldSeats = soldSeats; }

     public static EventDtoBuilder builder() {
          return new EventDtoBuilder();
     }

     public static class EventDtoBuilder {
          private String id;
          private String name;
          private String venueName;
          private long startTime;
          private long endTime;
          private String description;
          private double price;
          private int totalSeats;
          private int availableSeats;
          private int soldSeats;

          public EventDtoBuilder id(String id) { this.id = id; return this; }
          public EventDtoBuilder name(String name) { this.name = name; return this; }
          public EventDtoBuilder venueName(String venueName) { this.venueName = venueName; return this; }
          public EventDtoBuilder startTime(long startTime) { this.startTime = startTime; return this; }
          public EventDtoBuilder endTime(long endTime) { this.endTime = endTime; return this; }
          public EventDtoBuilder description(String description) { this.description = description; return this; }
          public EventDtoBuilder price(double price) { this.price = price; return this; }
          public EventDtoBuilder totalSeats(int totalSeats) { this.totalSeats = totalSeats; return this; }
          public EventDtoBuilder availableSeats(int availableSeats) { this.availableSeats = availableSeats; return this; }
          public EventDtoBuilder soldSeats(int soldSeats) { this.soldSeats = soldSeats; return this; }

          public EventDto build() {
               return new EventDto(id, name, venueName, startTime, endTime, description, price, totalSeats, availableSeats, soldSeats);
          }
     }
}
