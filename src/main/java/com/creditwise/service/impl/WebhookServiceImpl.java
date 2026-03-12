package com.creditwise.service.impl;

import com.creditwise.entity.PaystackWebhookEvent;
import com.creditwise.repository.PaystackWebhookEventRepository;
import com.creditwise.service.WebhookService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final PaystackWebhookEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void storeWebhookEvent(String payload, String signature) {

        try {

            JsonNode json = objectMapper.readTree(payload);

            String eventType = json.get("event").asText();
            String eventId = json.get("data").get("id").asText();
            String reference = json.get("data").get("reference").asText();

            if (eventRepository.findByEventId(eventId).isPresent()) {
                return;
            }

            PaystackWebhookEvent event = new PaystackWebhookEvent();

            event.setEventId(eventId);
            event.setEventType(eventType);
            event.setReference(reference);
            event.setSignature(signature);
            event.setPayload(payload);
            event.setReceivedAt(LocalDateTime.now());
            event.setProcessed(false);

            eventRepository.save(event);

        } catch (Exception e) {
            throw new RuntimeException("Failed to store webhook event");
        }
    }
}