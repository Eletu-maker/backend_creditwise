package com.creditwise.repository;

import java.util.List;

import com.creditwise.entity.PaystackWebhookEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaystackWebhookEventRepository extends JpaRepository<PaystackWebhookEvent, UUID> {

    Optional<PaystackWebhookEvent> findByReference(String reference);
    Optional<PaystackWebhookEvent> findByEventId(String eventId);
    List<PaystackWebhookEvent> findByProcessedFalse();
}