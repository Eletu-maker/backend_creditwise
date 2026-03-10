package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-test-email")
    public ResponseEntity<ApiResponse<String>> sendTestEmail(@RequestParam String email) {
        try {
            emailService.sendEmail(
                email, 
                "Test Email from CreditWise", 
                "<h1>Email Configuration Test</h1><p>If you received this email, your email configuration is working correctly!</p>"
            );
            return ResponseEntity.ok(ApiResponse.success("Test email sent successfully", "Test email sent to: " + email));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to send email: " + e.getMessage()));
        }
    }
}
