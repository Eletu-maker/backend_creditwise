package com.creditwise.service;

import com.creditwise.dto.CreditPlanDto;
import com.creditwise.entity.CreditPlan;

import java.util.List;
import java.util.UUID;

public interface CreditPlanService {
    CreditPlan createCreditPlan(CreditPlanDto creditPlanDto);
    CreditPlan getPlanById(UUID planId);
    List<CreditPlan> getPlansForCurrentUser();
    CreditPlan updateCreditPlan(UUID planId, CreditPlanDto creditPlanDto);
    List<CreditPlan> getPlansByClient(UUID clientId);
    List<CreditPlan> getPlansByOfficer(UUID officerId);
}