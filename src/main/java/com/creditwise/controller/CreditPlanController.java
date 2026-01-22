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
    public ResponseEntity<ApiResponse<List<CreditPlanDto>>> getMyCreditPlans() {
        List<CreditPlan> plans = creditPlanService.getPlansForCurrentUser();
        List<CreditPlanDto> dtos = plans.stream()
                .map(CreditPlanDto::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos, "Credit plans retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CreditPlanDto>> getCreditPlanById(@PathVariable UUID id, Authentication authentication) {
        CreditPlan plan = creditPlanService.getPlanById(id);
        
        // Check if the current user (officer) is assigned to the client of this plan
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if ("ROLE_OFFICER".equals(userDetails.getAuthorities().iterator().next().getAuthority())) {
            UUID officerId = userDetails.getUserId();
            UUID clientId = plan.getClient().getId();
            
            boolean isAssigned = assignmentService.findActiveAssignmentByOfficerIdAndClientId(officerId, clientId).isPresent();
            if (!isAssigned) {
                return ResponseEntity.status(403).body(ApiResponse.error("You are not assigned to this client"));
            }
        }
        
        CreditPlanDto dto = CreditPlanDto.fromEntity(plan);
        return ResponseEntity.ok(ApiResponse.success(dto, "Credit plan retrieved successfully"));
    }
    
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<List<CreditPlanDto>>> getCreditPlansByClient(@PathVariable UUID clientId, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Verify that the officer is assigned to the client
        boolean isAssigned = assignmentService.findActiveAssignmentByOfficerIdAndClientId(officerId, clientId).isPresent();
        
        if (!isAssigned) {
            return ResponseEntity.status(403).body(ApiResponse.error("You are not assigned to this client"));
        }
        
        List<CreditPlan> plans = creditPlanService.getPlansByClient(clientId);
        List<CreditPlanDto> dtos = plans.stream()
                .map(CreditPlanDto::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos, "Credit plans retrieved successfully"));
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