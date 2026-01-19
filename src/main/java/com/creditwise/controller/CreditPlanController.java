package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.CreditPlanDto;
import com.creditwise.entity.CreditPlan;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.CreditPlanService;
import com.creditwise.service.OfficerClientAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CreditPlanController {

    @Autowired
    private CreditPlanService creditPlanService;
    
    @Autowired
    private OfficerClientAssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CreditPlan>> createCreditPlan(@Valid @RequestBody CreditPlanDto creditPlanDto, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Verify that the officer is assigned to the client
        UUID clientId = UUID.fromString(creditPlanDto.getClientId());
        boolean isAssigned = assignmentService.findActiveAssignmentByOfficerIdAndClientId(officerId, clientId).isPresent();
        
        if (!isAssigned) {
            return ResponseEntity.status(403).body(ApiResponse.error("You are not assigned to this client"));
        }
        
        // Set the officer ID from the token
        creditPlanDto.setOfficerId(officerId.toString());
        
        CreditPlan plan = creditPlanService.createCreditPlan(creditPlanDto);
        return ResponseEntity.ok(ApiResponse.success(plan, "Credit plan created successfully"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<List<CreditPlan>>> getMyCreditPlans() {
        List<CreditPlan> plans = creditPlanService.getPlansForCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(plans, "Credit plans retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CreditPlan>> getCreditPlanById(@PathVariable UUID id) {
        CreditPlan plan = creditPlanService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan, "Credit plan retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CreditPlan>> updateCreditPlan(@PathVariable UUID id, @Valid @RequestBody CreditPlanDto creditPlanDto, Authentication authentication) {
        // Extract officer ID from the security context to ensure only the plan creator can update
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Set the officer ID from the token to ensure consistency
        creditPlanDto.setOfficerId(officerId.toString());
        
        CreditPlan plan = creditPlanService.updateCreditPlan(id, creditPlanDto);
        return ResponseEntity.ok(ApiResponse.success(plan, "Credit plan updated successfully"));
    }
}