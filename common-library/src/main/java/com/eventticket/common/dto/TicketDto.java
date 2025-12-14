package com.eventticket.common.dto;

public class TicketDto {
     private String id;
     private String eventId;
     private String seatId;
     private String userId;
     private String qrCode;
     private String status;
     private long createdAt;

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

     public String getQrCode() {
           return qrCode;
     }

     public void setQrCode(String qrCode) {
           this.qrCode = qrCode;
     }

     public String getStatus() {
           return status;
     }

     public void setStatus(String status) {
           this.status = status;
     }

     public long getCreatedAt() {
           return createdAt;
     }

     public void setCreatedAt(long createdAt) {
           this.createdAt = createdAt;
     }

     public static TicketDtoBuilder builder() {
           return new TicketDtoBuilder();
     }

     public static class TicketDtoBuilder {
           private String id;
           private String eventId;
           private String seatId;
           private String userId;
           private String qrCode;
           private String status;
           private long createdAt;

           public TicketDtoBuilder id(String id) {
                 this.id = id;
                 return this;
           }

           public TicketDtoBuilder eventId(String eventId) {
                 this.eventId = eventId;
                 return this;
           }

           public TicketDtoBuilder seatId(String seatId) {
                 this.seatId = seatId;
                 return this;
           }

           public TicketDtoBuilder userId(String userId) {
                 this.userId = userId;
                 return this;
           }

           public TicketDtoBuilder qrCode(String qrCode) {
                 this.qrCode = qrCode;
                 return this;
           }

           public TicketDtoBuilder status(String status) {
                 this.status = status;
                 return this;
           }

           public TicketDtoBuilder createdAt(long createdAt) {
                 this.createdAt = createdAt;
                 return this;
           }

           public TicketDto build() {
                 TicketDto dto = new TicketDto();
                 dto.id = this.id;
                 dto.eventId = this.eventId;
                 dto.seatId = this.seatId;
                 dto.userId = this.userId;
                 dto.qrCode = this.qrCode;
                 dto.status = this.status;
                 dto.createdAt = this.createdAt;
                 return dto;
           }
     }
}
