package com.creditwise.service.impl;

import com.creditwise.entity.CreditPlan;
import com.creditwise.entity.Plan;
import com.creditwise.entity.PlanAssignment;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.CreditPlanRepository;
import com.creditwise.repository.PlanAssignmentRepository;
import com.creditwise.repository.PlanRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanAssignmentRepository planAssignmentRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CreditPlanRepository creditPlanRepository;

    @Override
    public Plan createPlan(String title, String description, Integer durationInDays) {
        Plan plan = Plan.builder()
                .title(title)
                .description(description)
                .durationInDays(durationInDays)
                .planStatus(Plan.PlanStatus.ACTIVE)
                .build();

        return planRepository.save(plan);
    }
    
    @Override
    public Plan createPlanAndAssign(String title, String description, Integer durationInDays, String clientId) {
        Plan plan = Plan.builder()
                .title(title)
                .description(description)
                .durationInDays(durationInDays)
                .planStatus(Plan.PlanStatus.ACTIVE)
                .build();
        
        Plan savedPlan = planRepository.save(plan);
        
        return savedPlan;
    }

    @Override
    public Plan getPlanById(UUID planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @Override
    public List<Plan> getPlansByStatus(Plan.PlanStatus status) {
        return planRepository.findByPlanStatus(status);
    }

    @Override
    public Plan updatePlan(UUID planId, String title, String description, Integer durationInDays, Plan.PlanStatus planStatus) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));

        if (title != null) plan.setTitle(title);
        if (description != null) plan.setDescription(description);
        if (durationInDays != null) plan.setDurationInDays(durationInDays);
        if (planStatus != null) plan.setPlanStatus(planStatus);

        return planRepository.save(plan);
    }

    @Override
    public void deletePlan(UUID planId) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Plan", "id", planId);
        }
        planRepository.deleteById(planId);
    }

    @Override
    public PlanAssignment assignPlanToClient(UUID planId, UUID clientId, UUID officerId) {
        // Check if the client already has an active assignment (not COMPLETED)
        Optional<PlanAssignment> activeAssignment = planAssignmentRepository.findTopByClientIdAndAssignmentStatusNotOrderByCreatedAtDesc(
            clientId, PlanAssignment.AssignmentStatus.COMPLETED);
            
        if (activeAssignment.isPresent()) {
            throw new RuntimeException("Client already has an active plan assignment. Cannot assign a new plan until current one is completed.");
        }

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        PlanAssignment planAssignment = PlanAssignment.builder()
                .client(client)
                .officer(officer)
                .plan(plan)
                .assignmentStatus(PlanAssignment.AssignmentStatus.PENDING)
                .progressPercentage(0)
                .build();
        
        PlanAssignment savedAssignment = planAssignmentRepository.save(planAssignment);
        
        // Also create a corresponding CreditPlan entry so the client can see it
        CreditPlan creditPlan = CreditPlan.builder()
                .client(client)
                .officer(officer)
                .title(plan.getTitle())
                .description(plan.getDescription())
                .planStatus(CreditPlan.PlanStatus.ACTIVE)
                .build();
        
        creditPlanRepository.save(creditPlan);
        
        return savedAssignment;
    }

    @Override
    public List<PlanAssignment> getAssignmentsByClientId(UUID clientId) {
        return planAssignmentRepository.findByClientId(clientId);
    }

    @Override
    public List<PlanAssignment> getAssignmentsByOfficerId(UUID officerId) {
        return planAssignmentRepository.findByOfficerId(officerId);
    }

    @Override
    public PlanAssignment getPlanAssignmentById(UUID assignmentId) {
        return planAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("PlanAssignment", "id", assignmentId));
    }
    
    @Override
    public PlanAssignment updateAssignmentStatus(UUID assignmentId, PlanAssignment.AssignmentStatus status) {
        PlanAssignment assignment = planAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("PlanAssignment", "id", assignmentId));

        assignment.setAssignmentStatus(status);
        if (status == PlanAssignment.AssignmentStatus.COMPLETED) {
            assignment.setProgressPercentage(100);
        }

        PlanAssignment savedAssignment = planAssignmentRepository.save(assignment);
        
        // Update the corresponding CreditPlan status
        updateCorrespondingCreditPlan(assignment, status);
        
        return savedAssignment;
    }
    
    private void updateCorrespondingCreditPlan(PlanAssignment assignment, PlanAssignment.AssignmentStatus status) {
        // Find the corresponding CreditPlan based on client and plan title/description
        // Since we created the CreditPlan with the same title as the Plan, we can match them
        List<CreditPlan> creditPlans = creditPlanRepository.findByClientAndTitle(
                assignment.getClient(), assignment.getPlan().getTitle());
        
        for (CreditPlan creditPlan : creditPlans) {
            if (status == PlanAssignment.AssignmentStatus.COMPLETED) {
                creditPlan.setPlanStatus(CreditPlan.PlanStatus.COMPLETED);
            } else if (status == PlanAssignment.AssignmentStatus.ENDED) {
                creditPlan.setPlanStatus(CreditPlan.PlanStatus.ARCHIVED); // or COMPLETED depending on your business logic
            }
            creditPlanRepository.save(creditPlan);
        }
    }

    @Override
    public PlanAssignment updateAssignmentProgress(UUID assignmentId, Integer progressPercentage) {
        PlanAssignment assignment = planAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("PlanAssignment", "id", assignmentId));

        if (progressPercentage != null) {
            assignment.setProgressPercentage(Math.max(0, Math.min(100, progressPercentage))); // Ensure percentage is between 0 and 100
        }

        // Update status based on progress percentage
        if (progressPercentage != null) {
            if (progressPercentage >= 100) {
                assignment.setAssignmentStatus(PlanAssignment.AssignmentStatus.COMPLETED);
            } else if (progressPercentage > 0) {
                assignment.setAssignmentStatus(PlanAssignment.AssignmentStatus.IN_PROGRESS);
            }
        }

        PlanAssignment savedAssignment = planAssignmentRepository.save(assignment);
        
        // Update the corresponding CreditPlan status
        updateCorrespondingCreditPlan(assignment, assignment.getAssignmentStatus());
        
        return savedAssignment;
    }

    @Override
    public boolean hasActiveAssignment(UUID clientId) {
        Optional<PlanAssignment> activeAssignment = planAssignmentRepository.findTopByClientIdAndAssignmentStatusNotOrderByCreatedAtDesc(
            clientId, PlanAssignment.AssignmentStatus.COMPLETED);
        return activeAssignment.isPresent();
    }
    
    @Override
    public boolean hasActiveAssignmentWithDifferentOfficer(UUID clientId, UUID officerId) {
        // Find the most recent assignment that is not completed
        Optional<PlanAssignment> activeAssignment = planAssignmentRepository.findTopByClientIdAndAssignmentStatusNotOrderByCreatedAtDesc(
            clientId, PlanAssignment.AssignmentStatus.COMPLETED);
        
        // Return true if there's an active assignment with a different officer
        return activeAssignment.isPresent() && 
               !activeAssignment.get().getOfficer().getId().equals(officerId);
    }
    
}