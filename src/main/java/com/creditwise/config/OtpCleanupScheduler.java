package com.creditwise.config;

import com.creditwise.service.OtpAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OtpCleanupScheduler {

    @Autowired
    private OtpAuthService otpAuthService;

    /**
     * Cleanup expired OTPs every 10 minutes
     */
    @Scheduled(fixedRate = 600000) // 10 minutes = 600,000 milliseconds
    public void cleanupExpiredOtps() {
        otpAuthService.cleanExpiredOtps();
    }
}