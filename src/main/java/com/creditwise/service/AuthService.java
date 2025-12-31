package com.creditwise.service;

import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;

public interface AuthService {

    JwtResponse authenticateUser(LoginRequest loginRequest);

    JwtResponse registerClient(RegisterClientRequest registerRequest);
    
    // Method for OTP-based admin login
    JwtResponse authenticateAdminWithOtp(String email, String otpCode);
    
    // Method to initiate OTP generation for admin login
    String initiateAdminOtpLogin(String email);
    
    // Method for logout functionality
    void logout(String email);
}