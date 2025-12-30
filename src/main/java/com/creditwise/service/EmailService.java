package com.creditwise.service;

public interface EmailService {
    
    /**
     * Send OTP email to the specified email address
     */
    void sendOtpEmail(String to, String otpCode);
    
    /**
     * Send a generic email
     */
    void sendEmail(String to, String subject, String text);
}