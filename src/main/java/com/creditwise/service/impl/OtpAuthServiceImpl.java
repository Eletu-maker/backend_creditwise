package com.creditwise.service.impl;

import com.creditwise.entity.Otp;
import com.creditwise.repository.OtpRepository;
import com.creditwise.service.EmailService;
import com.creditwise.service.OtpAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpAuthServiceImpl implements OtpAuthService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Override
    @Transactional
    public String generateAndSendOtp(String email) {
        // Delete any existing OTPs for this email
        otpRepository.deleteByEmail(email);

        // Generate a 6-digit OTP
        String otpCode = generateOtpCode();
        
        // Create OTP entity
        Otp otp = Otp.builder()
                .email(email)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .used(false)
                .build();

        // Save OTP
        otpRepository.save(otp);

        // Send OTP via email
        sendOtpEmail(email, otpCode);

        return otpCode;
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otpCode) {
        return otpRepository.findByEmailAndOtpCode(email, otpCode)
                .map(otp -> {
                    if (otp.isValid()) {
                        // Mark OTP as used
                        otpRepository.markAsUsed(email, otpCode);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public void cleanExpiredOtps() {
        otpRepository.deleteExpiredOtps();
    }

    private String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String email, String otpCode) {
        // Send OTP via email service
        emailService.sendOtpEmail(email, otpCode);
    }
}