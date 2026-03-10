package com.creditwise.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.creditwise.service.SubscriptionSchedulerService;
import com.creditwise.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionSchedulerServiceImpl implements SubscriptionSchedulerService {

    private final SubscriptionService subscriptionService;

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // runs every day at midnight
    public void deactivateExpiredSubscriptions() {
        log.info("Running scheduled task to deactivate expired subscriptions");
        subscriptionService.deactivateExpiredSubscriptions();
    }
}