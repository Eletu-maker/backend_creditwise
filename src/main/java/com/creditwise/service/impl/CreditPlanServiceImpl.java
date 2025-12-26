package com.creditwise.service.impl;

import com.creditwise.dto.CreditPlanDto;
import com.creditwise.entity.CreditPlan;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.CreditPlanRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.CreditPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreditPlanServiceImpl implements CreditPlanService {

    @Autowired
    private CreditPlanRepository creditPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CreditPlan createCreditPlan(CreditPlanDto creditPlanDto) {
        User client = userRepository.findById(UUID.fromString(creditPlanDto.getClientId()))
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", creditPlanDto.getClientId()));

        User officer = userRepository.findById(UUID.fromString(creditPlanDto.getOfficerId()))
                .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", creditPlanDto.getOfficerId()));

        CreditPlan plan = CreditPlan.builder()
                .client(client)
                .officer(officer)
                .title(creditPlanDto.getTitle())
                .description(creditPlanDto.getDescription())
                .planStatus(CreditPlan.PlanStatus.valueOf(creditPlanDto.getPlanStatus()))
                .build();

        return creditPlanRepository.save(plan);
    }

    @Override
    public CreditPlan getPlanById(UUID planId) {
        return creditPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit plan", "id", planId));
    }

    @Override
    public List<CreditPlan> getPlansForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));
        
        if (currentUser.getRole() == User.Role.CLIENT) {
            return creditPlanRepository.findByClient(currentUser);
        } else if (currentUser.getRole() == User.Role.OFFICER) {
            return creditPlanRepository.findByOfficer(currentUser);
        }
        
        return List.of(); // Return empty list for admins or other roles
    }

    @Override
    public CreditPlan updateCreditPlan(UUID planId, CreditPlanDto creditPlanDto) {
        CreditPlan plan = creditPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit plan", "id", planId));

        plan.setTitle(creditPlanDto.getTitle());
        plan.setDescription(creditPlanDto.getDescription());
        
        if (creditPlanDto.getPlanStatus() != null) {
            plan.setPlanStatus(CreditPlan.PlanStatus.valueOf(creditPlanDto.getPlanStatus()));
        }

        return creditPlanRepository.save(plan);
    }

    @Override
    public List<CreditPlan> getPlansByClient(UUID clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        
        return creditPlanRepository.findByClient(client);
    }

    @Override
    public List<CreditPlan> getPlansByOfficer(UUID officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));
        
        return creditPlanRepository.findByOfficer(officer);
    }
}