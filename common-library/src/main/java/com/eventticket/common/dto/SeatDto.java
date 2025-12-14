package com.eventticket.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
     private String id;
     private String eventId;
     private String row;
     private int col;
     private String status; // AVAILABLE, BLOCKED, SOLD
     private String heldBy;
     private long heldUntil;

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

     public String getRow() {
           return row;
     }

     public void setRow(String row) {
           this.row = row;
     }

     public int getCol() {
           return col;
     }

     public void setCol(int col) {
           this.col = col;
     }

     public String getStatus() {
           return status;
     }

     public void setStatus(String status) {
           this.status = status;
     }

     public String getHeldBy() {
           return heldBy;
     }

     public void setHeldBy(String heldBy) {
           this.heldBy = heldBy;
     }

     public long getHeldUntil() {
           return heldUntil;
     }

     public void setHeldUntil(long heldUntil) {
           this.heldUntil = heldUntil;
     }

     public static SeatDtoBuilder builder() {
           return new SeatDtoBuilder();
     }

     public static class SeatDtoBuilder {
           private String id;
           private String eventId;
           private String row;
           private int col;
           private String status;
           private String heldBy;
           private long heldUntil;

           public SeatDtoBuilder id(String id) {
                 this.id = id;
                 return this;
           }

           public SeatDtoBuilder eventId(String eventId) {
                 this.eventId = eventId;
                 return this;
           }

           public SeatDtoBuilder row(String row) {
                 this.row = row;
                 return this;
           }

           public SeatDtoBuilder col(int col) {
                 this.col = col;
                 return this;
           }

           public SeatDtoBuilder status(String status) {
                 this.status = status;
                 return this;
           }

           public SeatDtoBuilder heldBy(String heldBy) {
                 this.heldBy = heldBy;
                 return this;
           }

           public SeatDtoBuilder heldUntil(long heldUntil) {
                 this.heldUntil = heldUntil;
                 return this;
           }

           public SeatDto build() {
                 SeatDto dto = new SeatDto();
                 dto.id = this.id;
                 dto.eventId = this.eventId;
                 dto.row = this.row;
                 dto.col = this.col;
                 dto.status = this.status;
                 dto.heldBy = this.heldBy;
                 dto.heldUntil = this.heldUntil;
                 return dto;
           }
     }
}
