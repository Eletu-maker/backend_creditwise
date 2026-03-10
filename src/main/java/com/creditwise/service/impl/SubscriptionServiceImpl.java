package com.creditwise.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import com.creditwise.dto.SubscriptionDto;
import com.creditwise.entity.Subscription;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.SubscriptionRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void activateSubscription(User user, BigDecimal amount) {
        log.info("Activating subscription for user: {}", user.getEmail());
        
        // Check if user already has an active subscription
        Optional<Subscription> existingSubscription = 
            subscriptionRepository.findActiveSubscriptionByUserId(user.getId(), LocalDate.now());
        
        if (existingSubscription.isPresent()) {
            // Extend existing subscription
            Subscription subscription = existingSubscription.get();
            subscription.setEndDate(subscription.getEndDate().plusMonths(1));
            subscriptionRepository.save(subscription);
            log.info("Extended existing subscription for user: {}", user.getEmail());
        } else {
            // Create new subscription
            Subscription subscription = Subscription.builder()
                    .user(user)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(1))
                    .subscriptionStatus(Subscription.SubscriptionStatus.ACTIVE)
                    .autoRenew(false)
                    .amount(amount)
                    .build();
            
            subscriptionRepository.save(subscription);
            log.info("Created new subscription for user: {}", user.getEmail());
        }
    }

    @Override
    public boolean isClientSubscribed(UUID userId) {
        Optional<Subscription> subscription = 
            subscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDate.now());
        
        return subscription.isPresent() && subscription.get().isActive();
    }

    @Override
    @Transactional
    public void deactivateExpiredSubscriptions() {
        log.info("Running scheduled task to deactivate expired subscriptions");
        
        List<Subscription> expiredSubscriptions = 
            subscriptionRepository.findExpiredSubscriptions(LocalDate.now());
        
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setSubscriptionStatus(Subscription.SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
            log.info("Deactivated expired subscription for user: {}", subscription.getUser().getEmail());
        }
        
        log.info("Deactivated {} expired subscriptions", expiredSubscriptions.size());
    }

    @Override
    public SubscriptionDto getActiveSubscription(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));
        
        return SubscriptionDto.fromEntity(subscription);
    }

    @Override
    @Transactional
    public void cancelSubscription(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));
        
        subscription.setSubscriptionStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
        
        log.info("Cancelled subscription for user: {}", user.getEmail());
    }
}