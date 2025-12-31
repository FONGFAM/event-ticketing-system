package com.eventticket.payment.service;

import com.eventticket.payment.entity.PaymentTransaction;
import com.eventticket.payment.repository.PaymentTransactionRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PaymentService {
      private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
      private final PaymentTransactionRepository transactionRepository;
      private final KafkaTemplate<String, String> kafkaTemplate;

      private static final String PAYMENT_CONFIRMED_TOPIC = "payment-confirmed";
      private static final String PAYMENT_FAILED_TOPIC = "payment-failed";

      public PaymentService(PaymentTransactionRepository transactionRepository,
                  KafkaTemplate<String, String> kafkaTemplate) {
            this.transactionRepository = transactionRepository;
            this.kafkaTemplate = kafkaTemplate;
      }

      public Map<String, Object> createPayment(String userId, String eventId, double amount, String paymentMethod) {
            logger.info("Creating payment: userId={}, eventId={}, amount={}", userId, eventId, amount);

            PaymentTransaction transaction = PaymentTransaction.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .amount(amount)
                        .paymentMethod(paymentMethod)
                        .status("PENDING")
                        .build();

            PaymentTransaction saved = transactionRepository.save(transaction);
            String transactionId = "AUTO-" + saved.getId();
            saved.setStatus("CONFIRMED");
            saved.setTransactionId(transactionId);
            transactionRepository.save(saved);
            try {
                  publishPaymentConfirmedEvent(saved);
            } catch (Exception ex) {
                  logger.warn("Failed to publish payment confirmed event: paymentId={}, error={}", saved.getId(),
                              ex.getMessage());
            }
            logger.info("Payment auto-confirmed: paymentId={}", saved.getId());

            // Generate QR code for bank transfer
            Map<String, Object> result = new HashMap<>();
            result.put("paymentId", saved.getId());
            result.put("transactionId", transactionId);
            result.put("status", saved.getStatus());

            if ("BANK_TRANSFER".equalsIgnoreCase(paymentMethod)) {
                  try {
                        String qrCode = generateVietQR(saved.getId(), amount);
                        result.put("qrCode", qrCode);
                        result.put("bankInfo", getBankTransferInfo(saved.getId(), amount));
                  } catch (Exception e) {
                        logger.error("Failed to generate QR code: {}", e.getMessage());
                  }
            }

            return result;
      }

      public void confirmPayment(String paymentId, String transactionIdFromGateway) {
            logger.info("Confirming payment: paymentId={}, gatewayTransactionId={}", paymentId,
                        transactionIdFromGateway);

            PaymentTransaction transaction = transactionRepository.findById(paymentId)
                        .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

            // Simulate payment gateway call (in production, verify with actual gateway)
            boolean isValid = validatePaymentWithGateway(transactionIdFromGateway);

            if (isValid) {
                  transaction.setStatus("CONFIRMED");
                  transaction.setTransactionId(transactionIdFromGateway);
                  transactionRepository.save(transaction);

                  // Publish event
                  publishPaymentConfirmedEvent(transaction);
                  logger.info("Payment confirmed: paymentId={}", paymentId);
            } else {
                  transaction.setStatus("FAILED");
                  transactionRepository.save(transaction);
                  logger.warn("Payment validation failed: paymentId={}", paymentId);
                  publishPaymentFailedEvent(transaction);
            }
      }

      public PaymentTransaction getPaymentStatus(String paymentId) {
            return transactionRepository.findById(paymentId)
                        .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
      }

      public java.util.List<PaymentTransaction> getAllPayments() {
            return transactionRepository.findAll();
      }

      private boolean validatePaymentWithGateway(String transactionId) {
            // Mock validation - in production, call real payment gateway API
            logger.info("Validating payment with gateway: transactionId={}", transactionId);
            return !transactionId.contains("FAIL");
      }

      private void publishPaymentConfirmedEvent(PaymentTransaction transaction) {
            String message = String.format(
                        "{\"paymentId\": \"%s\", \"userId\": \"%s\", \"eventId\": \"%s\", \"amount\": %.2f}",
                        transaction.getId(), transaction.getUserId(), transaction.getEventId(),
                        transaction.getAmount());

            kafkaTemplate.send(PAYMENT_CONFIRMED_TOPIC, transaction.getId(), message);
            logger.info("Payment confirmed event published to Kafka");
      }

      private void publishPaymentFailedEvent(PaymentTransaction transaction) {
            String message = String.format("{\"paymentId\": \"%s\", \"userId\": \"%s\"}",
                        transaction.getId(), transaction.getUserId());

            kafkaTemplate.send(PAYMENT_FAILED_TOPIC, transaction.getId(), message);
            logger.info("Payment failed event published to Kafka");
      }

      private String generateVietQR(String paymentId, double amount) throws IOException {
            // Use VietQR API to generate proper EMVCo-format QR code
            String bankId = "970436"; // Vietcombank (ICB)
            String accountNo = "9368274801";
            String accountName = "PHAM QUANG PHONG";
            String memo = "Thanh toan " + paymentId.substring(0, Math.min(10, paymentId.length()));

            // Call VietQR API: https://api.vietqr.io/v2/generate
            String apiUrl = String.format(
                        "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%.0f&addInfo=%s&accountName=%s",
                        bankId,
                        accountNo,
                        amount,
                        java.net.URLEncoder.encode(memo, "UTF-8"),
                        java.net.URLEncoder.encode(accountName, "UTF-8"));

            logger.info("Generating VietQR using API: {}", apiUrl);

            // Return the image URL directly
            return apiUrl;
      }

      private Map<String, String> getBankTransferInfo(String paymentId, double amount) {
            Map<String, String> bankInfo = new HashMap<>();
            bankInfo.put("bankName", "Vietcombank");
            bankInfo.put("accountNumber", "9368274801");
            bankInfo.put("accountName", "PHAM QUANG PHONG");
            bankInfo.put("amount", String.format("%.0f", amount));
            bankInfo.put("content", "Thanh toan ve " + paymentId.substring(0, Math.min(10, paymentId.length())));
            return bankInfo;
      }
}
