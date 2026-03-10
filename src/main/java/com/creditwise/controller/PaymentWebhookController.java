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
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature
    ) {
        log.info("Received Paystack webhook");
        paymentService.processWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed");
    }

}