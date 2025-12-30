package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.dto.AdminOtpLoginRequest;
import com.creditwise.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register-client")
    public ResponseEntity<ApiResponse<JwtResponse>> registerClient(@Valid @RequestBody RegisterClientRequest registerRequest) {
        JwtResponse response = authService.registerClient(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Client registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }
    
    // Endpoint to initiate admin OTP login
    @PostMapping("/admin/initiate-otp-login")
    public ResponseEntity<ApiResponse<String>> initiateAdminOtpLogin(@RequestBody AdminOtpLoginRequest request) {
        String otpCode = authService.initiateAdminOtpLogin(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(otpCode, "OTP sent to admin email"));
    }
    
    // Endpoint for admin to verify OTP and login
    @PostMapping("/admin/verify-otp-login")
    public ResponseEntity<ApiResponse<JwtResponse>> verifyAdminOtpLogin(@RequestBody AdminOtpLoginRequest request) {
        JwtResponse response = authService.authenticateAdminWithOtp(request.getEmail(), request.getOtpCode());
        return ResponseEntity.ok(ApiResponse.success(response, "Admin authenticated successfully with OTP"));
    }
}