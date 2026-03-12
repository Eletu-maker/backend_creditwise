package com.creditwise.service.impl;

import com.creditwise.entity.PaystackWebhookEvent;
import com.creditwise.entity.Payment;
import com.creditwise.entity.Payment.PaymentStatus;
import com.creditwise.repository.PaystackWebhookEventRepository;
import com.creditwise.repository.PaymentRepository;
import com.creditwise.service.WebhookProcessorService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebhookProcessorServiceImpl implements WebhookProcessorService {

    private final PaystackWebhookEventRepository eventRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processWebhookEvent() {

        List<PaystackWebhookEvent> events =
                eventRepository.findByProcessedFalse();

        for (PaystackWebhookEvent event : events) {

            try {

                JsonNode json = objectMapper.readTree(event.getPayload());

                String eventType = event.getEventType();

                if ("charge.success".equals(eventType)) {

                    handleSuccessfulPayment(json);
                }

                event.setProcessed(true);

                eventRepository.save(event);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

   private void handleSuccessfulPayment(JsonNode json) {

    JsonNode data = json.get("data");

    String reference = data.get("reference").asText();

    BigDecimal amount =
            data.get("amount").decimalValue()
                    .divide(BigDecimal.valueOf(100));

    String email =
            data.get("customer").get("email").asText();

    Payment payment =
            paymentRepository.findByReference(reference)
                    .orElseThrow();

    if (payment.getAmount().compareTo(amount) != 0) {
        throw new RuntimeException("Amount mismatch detected");
    }

    String userEmail = payment.getUser().getEmail();

    if (!userEmail.equalsIgnoreCase(email)) {
        throw new RuntimeException("Email mismatch");
    }

    payment.setPaymentStatus(PaymentStatus.SUCCESS);

    paymentRepository.save(payment);
}

}