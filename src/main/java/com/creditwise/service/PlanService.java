package com.creditwise.service;

import com.creditwise.entity.Plan;
import com.creditwise.entity.PlanAssignment;

import java.util.List;
import java.util.UUID;

public interface PlanService {
    Plan createPlan(String title, String description, Integer durationInDays);
    Plan createPlanAndAssign(String title, String description, Integer durationInDays, String clientId);
    Plan getPlanById(UUID planId);
    List<Plan> getAllPlans();
    List<Plan> getPlansByStatus(Plan.PlanStatus status);
    Plan updatePlan(UUID planId, String title, String description, Integer durationInDays, Plan.PlanStatus planStatus);
    void deletePlan(UUID planId);
    
    PlanAssignment assignPlanToClient(UUID planId, UUID clientId, UUID officerId);
    List<PlanAssignment> getAssignmentsByClientId(UUID clientId);
    List<PlanAssignment> getAssignmentsByOfficerId(UUID officerId);
    PlanAssignment getPlanAssignmentById(UUID assignmentId);
    PlanAssignment updateAssignmentStatus(UUID assignmentId, PlanAssignment.AssignmentStatus status);
    PlanAssignment updateAssignmentProgress(UUID assignmentId, Integer progressPercentage);
    boolean hasActiveAssignment(UUID clientId);
    boolean hasActiveAssignmentWithDifferentOfficer(UUID clientId, UUID officerId);
}