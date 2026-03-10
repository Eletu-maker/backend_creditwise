package com.creditwise.controller;
import com.creditwise.dto.PaystackInitializeResponse;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.PaymentDto;
import com.creditwise.dto.PaymentInitializationRequest;
import com.creditwise.dto.PaymentVerificationRequest;
import com.creditwise.dto.SubscriptionDto;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.PaymentService;
import com.creditwise.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/initialize")
    @PreAuthorize("hasRole('CLIENT')")
    
public ResponseEntity<ApiResponse<PaystackInitializeResponse.DataResponse>> initializePayment(
        @Valid @RequestBody PaymentInitializationRequest request,
        Authentication authentication) {
    
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String email = userDetails.getUsername();
    
    PaystackInitializeResponse.DataResponse response = 
            paymentService.initializePayment(email, request.getAmount());
    
    return ResponseEntity.ok(
            ApiResponse.success(response, "Payment initialized successfully")
    );
}

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {
        
        paymentService.verifyAndRecordPayment(request.getReference());
        
        return ResponseEntity.ok(ApiResponse.success("Payment verified and subscription activated", 
                "Payment verified successfully"));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentHistory(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        List<PaymentDto> payments = paymentService.getPaymentHistory(userId);
        
        return ResponseEntity.ok(ApiResponse.success(payments, "Payment history retrieved successfully"));
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByReference(@PathVariable String reference) {
        PaymentDto payment = paymentService.getPaymentByReference(reference);
        
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment retrieved successfully"));
    }

    @GetMapping("/subscription/status")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<SubscriptionDto>> getSubscriptionStatus(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        SubscriptionDto subscription = subscriptionService.getActiveSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(subscription, "Subscription status retrieved successfully"));
    }

    @PostMapping("/subscription/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<String>> cancelSubscription(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        subscriptionService.cancelSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully", 
                "Subscription cancelled successfully"));
    }
}
