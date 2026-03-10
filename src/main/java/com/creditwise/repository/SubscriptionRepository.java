package com.creditwise.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.creditwise.entity.Subscription;
import com.creditwise.entity.User;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    
    Optional<Subscription> findByUser(User user);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.subscriptionStatus = 'ACTIVE' AND s.endDate >= :currentDate")
    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") UUID userId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT s FROM Subscription s WHERE s.subscriptionStatus = 'ACTIVE' AND s.endDate < :currentDate")
    List<Subscription> findExpiredSubscriptions(@Param("currentDate") LocalDate currentDate);
    
    List<Subscription> findByUserAndSubscriptionStatus(User user, Subscription.SubscriptionStatus status);
}
