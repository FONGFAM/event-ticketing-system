package com.eventticket.notificationanalytics.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

     @KafkaListener(topics = "ticket-created", groupId = "notification-service")
     public void onTicketCreated(String message) {
          log.info("Received TicketCreated event: {}", message);
          // Parse message and send email
          // Format: {"ticketId": "xxx", "userId": "xxx", "qrCode": "xxx"}
          sendTicketEmail(message);
     }

     @KafkaListener(topics = "payment-failed", groupId = "notification-service")
     public void onPaymentFailed(String message) {
          log.info("Received PaymentFailed event: {}", message);
          // Send notification about failed payment
          sendPaymentFailureEmail(message);
     }

     private void sendTicketEmail(String ticketInfo) {
          log.info("Sending ticket email...");
          // Implementation pending
     }

     private void sendPaymentFailureEmail(String paymentInfo) {
          log.info("Sending payment failure email...");
          // Implementation pending
     }
}
