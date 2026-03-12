package com.creditwise.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.creditwise.dto.PaystackInitializeResponse;
import com.creditwise.dto.PaymentDto;
import com.creditwise.entity.Payment;
import com.creditwise.entity.PaystackWebhookEvent;
import com.creditwise.entity.PaymentAuditLog;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;

import com.creditwise.repository.PaymentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.repository.PaystackWebhookEventRepository;
import com.creditwise.repository.PaymentAuditLogRepository;

import com.creditwise.service.PaymentService;
import com.creditwise.service.PaystackService;
import com.creditwise.service.SubscriptionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final PaystackService paystackService;
    private final SubscriptionService subscriptionService;
    private final PaystackWebhookEventRepository webhookEventRepository;

    @Value("${paystack.secret.key}")
    private String paystackSecret;

    @Override
    @Transactional
    public PaystackInitializeResponse.DataResponse initializePayment(String email, BigDecimal amount) {

        log.info("Initializing payment for email: {} with amount: {}", email, amount);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        PaystackInitializeResponse paystackResponse
                = paystackService.initializePayment(email, amount);

        String reference = paystackResponse.getData().getReference();

        Payment payment = Payment.builder()
                .user(user)
                .reference(reference)
                .amount(amount)
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .currency("NGN")
                .build();

        paymentRepository.save(payment);

        PaymentAuditLog auditLog = PaymentAuditLog.builder()
                .paymentId(payment.getId())
                .action("PAYMENT_INITIALIZED")
                .details("Payment initialized via Paystack")
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);

        log.info("Payment record created with reference: {}", reference);

        return paystackResponse.getData();
    }

    @Override
    @Transactional
    public void verifyAndRecordPayment(String reference) {

        log.info("Manually verifying payment with reference: {}", reference);

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found with reference: " + reference));

        String status = paystackService.verifyPayment(reference);

        if ("success".equalsIgnoreCase(status)) {

            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod("Paystack");

            paymentRepository.save(payment);

            PaymentAuditLog auditLog = PaymentAuditLog.builder()
                    .paymentId(payment.getId())
                    .action("PAYMENT_VERIFIED_MANUALLY")
                    .details("Payment verified manually using Paystack verify API")
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            subscriptionService.activateSubscription(
                    payment.getUser(),
                    payment.getAmount()
            );

            log.info("Manual payment verification successful for {}", reference);

        } else {

            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);

            PaymentAuditLog auditLog = PaymentAuditLog.builder()
                    .paymentId(payment.getId())
                    .action("PAYMENT_VERIFICATION_FAILED")
                    .details("Manual verification failed via Paystack API")
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            throw new RuntimeException("Payment verification failed");
        }
    }

    @Override
    @Transactional
    public void processWebhook(String payload, String signature) {

        log.info("Received Paystack webhook");

        String computedHash = Hashing.hmacSha512(paystackSecret.getBytes())
                .hashString(payload, StandardCharsets.UTF_8)
                .toString();

        if (!computedHash.equalsIgnoreCase(signature)) {
            log.error("Invalid Paystack webhook signature");
            throw new RuntimeException("Invalid webhook signature");
        }

        Map<String, Object> webhookData;

        try {
            webhookData = new ObjectMapper().readValue(payload, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid webhook payload");
        }

        String event = (String) webhookData.get("event");
        Map<String, Object> data = (Map<String, Object>) webhookData.get("data");

        String reference = (String) data.get("reference");

        if (webhookEventRepository.findByReference(reference).isPresent()) {
            log.warn("Replay webhook detected for reference: {}", reference);
            return;
        }

        PaystackWebhookEvent webhookEvent = PaystackWebhookEvent.builder()
                .eventType(event)
                .reference(reference)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .processed(false)
                .build();

        webhookEventRepository.save(webhookEvent);

        log.info("Processing webhook event {} for reference {}", event, reference);

        switch (event) {

            case "charge.success":
                handleSuccessfulPayment(reference);
                break;

            case "charge.failed":
                handleFailedPayment(reference);
                break;

            default:
                log.info("Unhandled Paystack event {}", event);
        }

        webhookEvent.setProcessed(true);
        webhookEventRepository.save(webhookEvent);
    }

    @Override
    public List<PaymentDto> getPaymentHistory(UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Payment> payments = paymentRepository.findPaymentHistoryByUserId(userId);

        return payments.stream()
                .map(PaymentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto getPaymentByReference(String reference) {

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return PaymentDto.fromEntity(payment);
    }

    private void handleSuccessfulPayment(String reference) {

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found with reference: " + reference));

        if (payment.getPaymentStatus() == Payment.PaymentStatus.SUCCESS) {
            log.info("Payment already processed for reference: {}", reference);
            return;
        }

        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("Paystack");

        paymentRepository.save(payment);

        PaymentAuditLog auditLog = PaymentAuditLog.builder()
                .paymentId(payment.getId())
                .action("PAYMENT_SUCCESS")
                .details("Payment confirmed via Paystack webhook")
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);

        subscriptionService.activateSubscription(
                payment.getUser(),
                payment.getAmount()
        );

        PaymentAuditLog subscriptionLog = PaymentAuditLog.builder()
                .paymentId(payment.getId())
                .action("SUBSCRIPTION_ACTIVATED")
                .details("Subscription activated after successful payment")
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(subscriptionLog);

        log.info("Payment successful and subscription activated for user: {}",
                payment.getUser().getEmail());
    }

    private void handleFailedPayment(String reference) {

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found with reference: " + reference));

        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);

        paymentRepository.save(payment);

        PaymentAuditLog auditLog = PaymentAuditLog.builder()
                .paymentId(payment.getId())
                .action("PAYMENT_FAILED")
                .details("Paystack reported payment failure")
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);

        log.warn("Payment failed for reference: {}", reference);
    }

    private void handleRefund(String reference) {

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found with reference: " + reference));

        payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);

        paymentRepository.save(payment);

        log.info("Payment refunded for reference: {}", reference);
    }
}
