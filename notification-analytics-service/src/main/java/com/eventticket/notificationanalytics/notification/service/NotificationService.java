package com.eventticket.notificationanalytics.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
     private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

     public NotificationService() {
     }

     @KafkaListener(topics = "ticket-created", groupId = "${spring.application.name}")
     public void onTicketCreated(String message) {
          logger.info("Received TicketCreated event: {}", message);
          // Parse message and send email
          // Format: {"ticketId": "xxx", "userId": "xxx", "qrCode": "xxx"}
          sendTicketEmail();
     }

     @KafkaListener(topics = "payment-failed", groupId = "${spring.application.name}")
     public void onPaymentFailed(String message) {
          logger.info("Received PaymentFailed event: {}", message);
          // Send notification about failed payment
          sendPaymentFailureEmail();
     }

     private void sendTicketEmail() {
          logger.info("Sending ticket email...");
          // Implementation pending
     }

     private void sendPaymentFailureEmail() {
          logger.info("Sending payment failure email...");
          // Implementation pending
     }
}
