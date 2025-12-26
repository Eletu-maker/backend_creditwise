package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.CreditPlanDto;
import com.creditwise.entity.CreditPlan;
import com.creditwise.service.CreditPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CreditPlanController {

    @Autowired
    private CreditPlanService creditPlanService;

    @PostMapping
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CreditPlan>> createCreditPlan(@Valid @RequestBody CreditPlanDto creditPlanDto) {
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
    public ResponseEntity<ApiResponse<CreditPlan>> updateCreditPlan(@PathVariable UUID id, @Valid @RequestBody CreditPlanDto creditPlanDto) {
        CreditPlan plan = creditPlanService.updateCreditPlan(id, creditPlanDto);
        return ResponseEntity.ok(ApiResponse.success(plan, "Credit plan updated successfully"));
    }
}