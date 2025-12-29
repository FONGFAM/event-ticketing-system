package com.eventticket.notificationanalytics.notification.config;

import com.eventticket.notificationanalytics.notification.entity.NotificationLog;
import com.eventticket.notificationanalytics.notification.repository.NotificationLogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
     private final NotificationLogRepository notificationLogRepository;

     public DataSeeder(NotificationLogRepository notificationLogRepository) {
          this.notificationLogRepository = notificationLogRepository;
     }

     @Override
     public void run(String... args) {
          if (notificationLogRepository.count() > 0) {
               return;
          }

          NotificationLog ticketLog = new NotificationLog();
          ticketLog.setEventType("ticket-created");
          ticketLog.setPayload("{\"ticketId\":\"ticket-1001\",\"userId\":\"user-1001\"}");
          ticketLog.setStatus("SENT");

          NotificationLog paymentLog = new NotificationLog();
          paymentLog.setEventType("payment-failed");
          paymentLog.setPayload("{\"paymentId\":\"payment-1002\",\"userId\":\"user-1002\"}");
          paymentLog.setStatus("FAILED");

          notificationLogRepository.saveAll(List.of(ticketLog, paymentLog));
     }
}
