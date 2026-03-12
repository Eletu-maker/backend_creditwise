package com.creditwise.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.creditwise.entity.Payment;
import com.creditwise.entity.User;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findByReference(String reference);

    List<Payment> findByUser(User user);

    List<Payment> findByUserAndPaymentStatus(User user, Payment.PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentHistoryByUserId(@Param("userId") UUID userId);
}