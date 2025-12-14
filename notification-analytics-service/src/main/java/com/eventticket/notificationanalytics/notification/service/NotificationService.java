package com.eventticket.notificationanalytics.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
     private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

     @KafkaListener(topics = "ticket-created", groupId = "notification-service")
     public void onTicketCreated(String message) {
          logger.info("Received TicketCreated event: {}", message);
          // Parse message and send email
          // Format: {"ticketId": "xxx", "userId": "xxx", "qrCode": "xxx"}
          sendTicketEmail();
     }

     @KafkaListener(topics = "payment-failed", groupId = "notification-service")
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
