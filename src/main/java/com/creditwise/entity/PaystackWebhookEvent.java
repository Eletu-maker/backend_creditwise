package com.creditwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaystackWebhookEvent {

    @Id
    @GeneratedValue
    private UUID id;

    private String eventType;

    private String reference;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime receivedAt;

    private boolean processed;
}