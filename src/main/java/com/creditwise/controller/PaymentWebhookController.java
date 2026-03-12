package com.creditwise.controller;

import com.creditwise.entity.Payment;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.PaymentRepository;
import com.creditwise.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository; // Inject the repository

    // Existing webhook route (delegates to PaymentService)
    @PostMapping("/webhook")
public ResponseEntity<String> handleWebhook(
        @RequestBody String payload,
        @RequestHeader("x-paystack-signature") String signature
) {

    paymentService.processWebhook(payload, signature);

    return ResponseEntity.ok("Webhook received");
}
/** 
    @PostMapping("/webhook/payment")
public ResponseEntity<String> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
    try {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String reference = (String) data.get("reference");
        String status = (String) data.get("status");

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Convert String to enum
        try {
            payment.setPaymentStatus(Payment.PaymentStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            log.warn("Unknown payment status received: {}", status);
            payment.setPaymentStatus(Payment.PaymentStatus.PENDING); // fallback
        }

        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Payment {} updated via webhook to {}", reference, status);
        return ResponseEntity.ok("Webhook received");

    } catch (Exception e) {
        log.error("Error processing webhook: {}", e.getMessage());
        return ResponseEntity.status(500).body("Error processing webhook");
    }
}
*/
}