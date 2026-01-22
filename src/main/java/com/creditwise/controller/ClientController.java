package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.ClientProfileDto;
import com.creditwise.dto.CreditPlanDto;
import com.creditwise.entity.CreditHealthScore;
import com.creditwise.entity.CreditPlan;
import com.creditwise.entity.User;
import com.creditwise.service.CreditHealthScoreService;
import com.creditwise.service.CreditPlanService;
import com.creditwise.service.UserService;
import com.creditwise.entity.ClientProfile;
import com.creditwise.repository.ClientProfileRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {

    @Autowired
    private UserService userService;

    @Autowired
    private CreditHealthScoreService creditHealthScoreService;

    @Autowired
    private CreditPlanService creditPlanService;

    @Autowired
    private ClientProfileRepository clientProfileRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<com.creditwise.dto.UserProfile>> getClientProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        com.creditwise.dto.UserProfile profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<com.creditwise.dto.UserProfile>> updateClientProfile(@RequestBody ClientProfileDto updatedProfile, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        // Find the client profile
        ClientProfile clientProfile = clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientProfile", "userId", userId));
        
        // Update only the allowed profile fields from the DTO
        if (updatedProfile.getPhoneNumber() != null) {
            clientProfile.setPhoneNumber(updatedProfile.getPhoneNumber());
        }
        if (updatedProfile.getAddress() != null) {
            clientProfile.setAddress(updatedProfile.getAddress());
        }
        if (updatedProfile.getCity() != null) {
            clientProfile.setCity(updatedProfile.getCity());
        }
        
        // Save the updated profile
        clientProfile = clientProfileRepository.save(clientProfile);
        
        // Return the updated profile
        com.creditwise.dto.UserProfile profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile updated successfully"));
    }
    
    @GetMapping("/credit-score")
    public ResponseEntity<ApiResponse<CreditHealthScore>> getCreditScore(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        CreditHealthScore score = creditHealthScoreService.getLatestScoreForClient(userId);
        return ResponseEntity.ok(ApiResponse.success(score));
    }

    @GetMapping("/credit-plans")
    public ResponseEntity<ApiResponse<List<CreditPlanDto>>> getCreditPlans(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        List<CreditPlan> plans = creditPlanService.getPlansByClient(userId);
        List<CreditPlanDto> planDtos = plans.stream()
                .map(CreditPlanDto::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(planDtos));
    }

    @GetMapping("/credit-plans/{planId}")
    public ResponseEntity<ApiResponse<CreditPlanDto>> getCreditPlanById(@PathVariable UUID planId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        CreditPlan plan = creditPlanService.getPlanById(planId);
        
        // Check if the plan belongs to the authenticated client
        if (!plan.getClient().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        CreditPlanDto planDto = CreditPlanDto.fromEntity(plan);
        return ResponseEntity.ok(ApiResponse.success(planDto));
    }

    @GetMapping("/credit-plans/{planId}/tasks")
    public ResponseEntity<ApiResponse<List<Object>>> getPlanTasks(@PathVariable UUID planId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        // In a real implementation, you would fetch the plan tasks
        // For now, returning an empty list
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
    
    @GetMapping("/plan-status")
    public ResponseEntity<ApiResponse<ClientProfile.PlanStatus>> getPlanStatus(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        ClientProfile clientProfile = clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientProfile", "userId", userId));
        
        return ResponseEntity.ok(ApiResponse.success(clientProfile.getPlanStatus()));
    }
}