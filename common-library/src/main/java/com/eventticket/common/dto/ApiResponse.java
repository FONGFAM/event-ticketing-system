package com.eventticket.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
     private boolean success;
     private String message;
     private T data;
     private String errorCode;
     private long timestamp;

     // Lombok workaround: explicit builder
     public static <T> ApiResponseBuilder<T> builder() {
          return new ApiResponseBuilder<>();
     }

     public static class ApiResponseBuilder<T> {
          private boolean success;
          private String message;
          private T data;
          private String errorCode;
          private long timestamp;

          public ApiResponseBuilder<T> success(boolean success) {
               this.success = success;
               return this;
          }

          public ApiResponseBuilder<T> message(String message) {
               this.message = message;
               return this;
          }

          public ApiResponseBuilder<T> data(T data) {
               this.data = data;
               return this;
          }

          public ApiResponseBuilder<T> errorCode(String errorCode) {
               this.errorCode = errorCode;
               return this;
          }

          public ApiResponseBuilder<T> timestamp(long timestamp) {
               this.timestamp = timestamp;
               return this;
          }

          public ApiResponse<T> build() {
               ApiResponse<T> response = new ApiResponse<>();
               response.success = this.success;
               response.message = this.message;
               response.data = this.data;
               response.errorCode = this.errorCode;
               response.timestamp = this.timestamp;
               return response;
          }
     }

     // Lombok workaround: explicit getters and setters
     public boolean isSuccess() {
          return success;
     }

     public void setSuccess(boolean success) {
          this.success = success;
     }

     public String getMessage() {
          return message;
     }

     public void setMessage(String message) {
          this.message = message;
     }

     public T getData() {
          return data;
     }

     public void setData(T data) {
          this.data = data;
     }

     public String getErrorCode() {
          return errorCode;
     }

     public void setErrorCode(String errorCode) {
          this.errorCode = errorCode;
     }

     public long getTimestamp() {
          return timestamp;
     }

     public void setTimestamp(long timestamp) {
          this.timestamp = timestamp;
     }

     public static <T> ApiResponse<T> ok(T data) {
          return ApiResponse.<T>builder()
                    .success(true)
                    .message("Success")
                    .data(data)
                    .timestamp(System.currentTimeMillis())
                    .build();
     }

     public static <T> ApiResponse<T> ok(T data, String message) {
          return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .timestamp(System.currentTimeMillis())
                    .build();
     }

     public static <T> ApiResponse<T> error(String message, String errorCode) {
          return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .errorCode(errorCode)
                    .timestamp(System.currentTimeMillis())
                    .build();
     }
}
