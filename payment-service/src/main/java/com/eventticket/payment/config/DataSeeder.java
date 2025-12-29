package com.eventticket.payment.config;

import com.eventticket.payment.entity.PaymentTransaction;
import com.eventticket.payment.repository.PaymentTransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
     private final PaymentTransactionRepository paymentTransactionRepository;

     public DataSeeder(PaymentTransactionRepository paymentTransactionRepository) {
          this.paymentTransactionRepository = paymentTransactionRepository;
     }

     @Override
     public void run(String... args) {
          if (paymentTransactionRepository.count() > 0) {
               return;
          }

          List<PaymentTransaction> transactions = List.of(
                    PaymentTransaction.builder()
                              .userId("user-1001")
                              .eventId("event-1001")
                              .amount(150.0)
                              .status("CONFIRMED")
                              .paymentMethod("CREDIT_CARD")
                              .transactionId("txn-1001")
                              .build(),
                    PaymentTransaction.builder()
                              .userId("user-1002")
                              .eventId("event-1002")
                              .amount(220.0)
                              .status("PENDING")
                              .paymentMethod("PAYPAL")
                              .transactionId("txn-1002")
                              .build()
          );

          paymentTransactionRepository.saveAll(transactions);
     }
}
