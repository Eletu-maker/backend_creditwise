package com.creditwise.service;

public interface OtpAuthService {
    
   
    String generateAndSendOtp(String email);
    
   
    boolean verifyOtp(String email, String otpCode);
    
    
    void cleanExpiredOtps();
}