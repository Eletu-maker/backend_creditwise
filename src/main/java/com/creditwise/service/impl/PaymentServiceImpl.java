package com.creditwise.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.creditwise.dto.PaystackInitializeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.creditwise.dto.PaymentDto;
import com.creditwise.entity.Payment;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.PaymentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.PaymentService;
import com.creditwise.service.PaystackService;
import com.creditwise.service.SubscriptionService;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaystackService paystackService;
    private final SubscriptionService subscriptionService;
     @Value("${paystack.secret.key}")
    private String paystackSecret;


    @Override
    @Transactional
    public PaystackInitializeResponse.DataResponse initializePayment(String email, BigDecimal amount) {

    log.info("Initializing payment for email: {} with amount: {}", email, amount);

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

    // Initialize payment with Paystack
    PaystackInitializeResponse paystackResponse = paystackService.initializePayment(email, amount);

    String reference = paystackResponse.getData().getReference();

    // Create payment record
    Payment payment = Payment.builder()
            .user(user)
            .reference(reference)
            .amount(amount)
            .paymentStatus(Payment.PaymentStatus.PENDING)
            .currency("NGN")
            .build();

    paymentRepository.save(payment);

    log.info("Payment record created with reference: {}", reference);

    return paystackResponse.getData();
}
    @Override
    @Transactional
    public void verifyAndRecordPayment(String reference) {
        log.info("Verifying payment with reference: {}", reference);
        
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with reference: " + reference));
        
        // Verify payment with Paystack
        String status = paystackService.verifyPayment(reference);
        
        if ("success".equalsIgnoreCase(status)) {
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod("Paystack");
            paymentRepository.save(payment);
            
            // Activate subscription for the user
            subscriptionService.activateSubscription(payment.getUser(), payment.getAmount());
            
            log.info("Payment verified and subscription activated for user: {}", payment.getUser().getEmail());
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            log.warn("Payment verification failed for reference: {}", reference);
            throw new RuntimeException("Payment verification failed");
        }
    }

    @Override
    public List<PaymentDto> getPaymentHistory(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<Payment> payments = paymentRepository.findPaymentHistoryByUserId(userId);
        
        return payments.stream()
                .map(PaymentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with reference: " + reference));
        
        return PaymentDto.fromEntity(payment);
    }


    @Override
    @Transactional
    public void processWebhook(Map<String, Object> payload, String signature) {

        log.info("Received Paystack webhook event");

        // Note: This signature verification might not work correctly with Map.toString()
        // Consider passing the raw JSON string instead of parsed Map for verification
        String computedHash = Hashing.hmacSha512(paystackSecret.getBytes())
                .hashString(payload.toString(), StandardCharsets.UTF_8)
                .toString();

        if (!computedHash.equals(signature)) {
            log.error("Invalid Paystack webhook signature");
            throw new RuntimeException("Invalid webhook signature");
        }

        String event = (String) payload.get("event");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        
        if (data == null) {
            log.error("Webhook payload missing 'data' field");
            throw new RuntimeException("Invalid webhook payload");
        }
        
        String reference = (String) data.get("reference");

        log.info("Webhook event: {} for reference: {}", event, reference);

        switch (event) {
            case "charge.success":
                handleSuccessfulPayment(reference);
                break;
            case "charge.failed":
                handleFailedPayment(reference);
                break;
            case "refund.processed":
                handleRefund(reference);
                break;
            default:
                log.warn("Unhandled Paystack event: {}", event);
        }
    }

    private void handleSuccessfulPayment(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with reference: " + reference));

        // Prevent duplicate webhook processing
        if (payment.getPaymentStatus() == Payment.PaymentStatus.SUCCESS) {
            log.info("Payment already processed for reference: {}", reference);
            return;
        }

        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("Paystack");

        paymentRepository.save(payment);

        // Activate subscription
        subscriptionService.activateSubscription(payment.getUser(), payment.getAmount());

        log.info("Payment successful and subscription activated for user: {}",
                payment.getUser().getEmail());
    }

    private void handleFailedPayment(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with reference: " + reference));

        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        paymentRepository.save(payment);

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
