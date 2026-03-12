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
public class PaymentAuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID paymentId;

    private String action;

    private String details;

    private LocalDateTime createdAt;
}