package com.creditwise.repository;

import com.creditwise.entity.PaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentAuditLogRepository extends JpaRepository<PaymentAuditLog, UUID> {
}