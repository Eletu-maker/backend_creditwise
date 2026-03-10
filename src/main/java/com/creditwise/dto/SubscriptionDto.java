package com.creditwise.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.creditwise.entity.Subscription;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    
    private UUID id;
    private UUID userId;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String subscriptionStatus;
    private boolean autoRenew;
    private BigDecimal amount;
    private boolean isActive;
    
    public static SubscriptionDto fromEntity(Subscription subscription) {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .userEmail(subscription.getUser().getEmail())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .subscriptionStatus(subscription.getSubscriptionStatus().name())
                .autoRenew(subscription.isAutoRenew())
                .amount(subscription.getAmount())
                .isActive(subscription.isActive())
                .build();
    }
}
