package com.creditwise.service;

import java.util.UUID;
import java.math.BigDecimal;
import com.creditwise.dto.SubscriptionDto;
import com.creditwise.entity.User;

public interface SubscriptionService {

    void activateSubscription(User user, BigDecimal amount);

    boolean isClientSubscribed(UUID userId);

    void deactivateExpiredSubscriptions();
    
    SubscriptionDto getActiveSubscription(UUID userId);
    
    void cancelSubscription(UUID userId);
}
