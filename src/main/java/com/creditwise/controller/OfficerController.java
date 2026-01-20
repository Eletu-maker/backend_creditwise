package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.AppointmentDto;
import com.creditwise.dto.ClientProfileDto;
import com.creditwise.entity.Appointment;
import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.ClientProfile;
import com.creditwise.repository.AppointmentRepository;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.AppointmentService;
import com.creditwise.service.OfficerClientAssignmentService;

import com.creditwise.repository.ClientProfileRepository;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.service.PlanService;
import com.creditwise.dto.PlanDto;
import com.creditwise.dto.PlanAssignmentDto;
import com.creditwise.entity.Plan;
import com.creditwise.entity.PlanAssignment;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/officer/")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('OFFICER')")
public class OfficerController {

    @Autowired
    private OfficerClientAssignmentService assignmentService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ClientProfileRepository clientProfileRepository;
    
    @Autowired
    private PlanService planService;
    

    


    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<List<OfficerClientAssignment>>> getMyAssignedClients(Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        List<OfficerClientAssignment> assignments = assignmentService.findByOfficerIdAndAssignmentStatus(officerId, OfficerClientAssignment.AssignmentStatus.ACTIVE);
        return ResponseEntity.ok(ApiResponse.success(assignments, "Assigned clients retrieved successfully"));
    }

    @GetMapping("/notifications/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getNotifiedAppointments(Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Get appointments for this officer that haven't been notified yet
        List<Appointment> appointments = appointmentService.getAppointmentsForOfficer(officerId);
        List<AppointmentDto> appointmentDtos = appointments.stream()
                .filter(app -> !app.isNotified()) // Only show unnotified appointments
                .map(app -> AppointmentDto.fromEntity(app))
                .collect(Collectors.toList());
        
        // Mark these appointments as notified
        for (Appointment appointment : appointments) {
            if (!appointment.isNotified()) {
                appointment.setNotified(true);
                appointmentRepository.save(appointment); // Direct save to update notification status
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success(appointmentDtos, "Notified appointments retrieved successfully"));
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<ApiResponse<List<OfficerClientAssignment>>> getClientById(@PathVariable UUID clientId, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        List<OfficerClientAssignment> assignments = assignmentService.findByOfficerIdAndClientId(officerId, clientId);
        
        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException("Assignment", "officerId and clientId", officerId + " and " + clientId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(assignments, "Client assignment history retrieved successfully"));
    }

    @PutMapping("/clients/{clientId}")
    public ResponseEntity<ApiResponse<ClientProfileDto>> updateClientProfile(@PathVariable UUID clientId, @RequestBody ClientProfileDto updatedProfile, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Check if the officer has an active assignment to this client
        Optional<OfficerClientAssignment> activeAssignmentOpt = assignmentService.findActiveAssignmentByOfficerIdAndClientId(officerId, clientId);
        if (activeAssignmentOpt.isEmpty()) {
            return ResponseEntity.status(403).build();
        }
        
        // Only allow updating the plan status
        ClientProfile existingProfile = clientProfileRepository.findByUserId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientProfile", "userId", clientId));
        
        // Update only the plan status
        existingProfile.setPlanStatus(updatedProfile.getPlanStatus());
        existingProfile = clientProfileRepository.save(existingProfile);
        
        ClientProfileDto responseDto = ClientProfileDto.fromEntity(existingProfile);
        return ResponseEntity.ok(ApiResponse.success(responseDto, "Client profile updated successfully"));
    }
    



    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<Plan>> createPlan(@RequestBody PlanDto planDto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String createdBy = userDetails.getUsername();
        UUID officerId = userDetails.getUserId();
        
        Plan plan = planService.createPlan(planDto.getTitle(), planDto.getDescription(), planDto.getDurationInDays());
        
        // If clientId is provided, assign the plan to the client immediately
        if (planDto.getClientId() != null && !planDto.getClientId().trim().isEmpty()) {
            UUID clientId = UUID.fromString(planDto.getClientId());
            
            // Check if the officer has an active assignment to this client
            Optional<OfficerClientAssignment> activeAssignmentOpt = assignmentService.findActiveAssignmentByOfficerIdAndClientId(officerId, clientId);
            if (activeAssignmentOpt.isEmpty()) {
                return ResponseEntity.status(403).build();
            }
            
            // Check if the client already has an active assignment with a different officer
            if (planService.hasActiveAssignmentWithDifferentOfficer(clientId, officerId)) {
                return ResponseEntity.status(400).body(ApiResponse.error("Client already has an active plan assignment with a different officer. Cannot assign a new plan until current one is completed."));
            }
            
            // Assign the newly created plan to the client
            planService.assignPlanToClient(plan.getId(), clientId, officerId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(plan, "Plan created successfully"));
    }
    
    @PutMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<Plan>> updatePlan(@PathVariable UUID planId, @RequestBody PlanDto planDto, Authentication authentication) {
        Plan updatedPlan = planService.updatePlan(
            planId,
            planDto.getTitle(),
            planDto.getDescription(),
            planDto.getDurationInDays(),
            planDto.getPlanStatus()
        );
        
        return ResponseEntity.ok(ApiResponse.success(updatedPlan, "Plan updated successfully"));
    }
    
    @PostMapping("/assign-plan")
    public ResponseEntity<ApiResponse<PlanAssignmentDto>> assignPlanToClient(@RequestBody PlanAssignmentDto requestDto, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Check if the officer has an active assignment to this client
        Optional<OfficerClientAssignment> activeAssignmentOpt = assignmentService.findActiveAssignmentByOfficerIdAndClientId(officerId, requestDto.getClientId());
        if (activeAssignmentOpt.isEmpty()) {
            return ResponseEntity.status(403).build();
        }
        
        // Check if the client already has an active assignment with a different officer
        if (planService.hasActiveAssignmentWithDifferentOfficer(requestDto.getClientId(), officerId)) {
            return ResponseEntity.status(400).body(ApiResponse.error("Client already has an active plan assignment with a different officer. Cannot assign a new plan until current one is completed."));
        }
        
        PlanAssignment planAssignment = planService.assignPlanToClient(requestDto.getPlanId(), requestDto.getClientId(), officerId);
        PlanAssignmentDto responseDto = PlanAssignmentDto.fromEntity(planAssignment);
        
        return ResponseEntity.ok(ApiResponse.success(responseDto, "Plan assigned to client successfully"));
    }
    
    @GetMapping("/client-plans/{clientId}")
    public ResponseEntity<ApiResponse<List<PlanAssignment>>> getClientPlans(@PathVariable UUID clientId, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Check if the officer has any assignment to this client (active or inactive)
        List<OfficerClientAssignment> clientAssignments = assignmentService.findByOfficerIdAndClientId(officerId, clientId);
        if (clientAssignments.isEmpty()) {
            return ResponseEntity.status(403).build();
        }
        
        List<PlanAssignment> planAssignments = planService.getAssignmentsByClientId(clientId);
        
        return ResponseEntity.ok(ApiResponse.success(planAssignments, "Client plan assignments retrieved successfully"));
    }
    
    @PostMapping("/end-plan-assignment/{planAssignmentId}")
    public ResponseEntity<ApiResponse<String>> endPlanAssignment(
            @PathVariable UUID planAssignmentId, 
            @RequestParam(defaultValue = "COMPLETED") String status,
            Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Get the plan assignment
        PlanAssignment planAssignment = planService.getPlanAssignmentById(planAssignmentId);
        
        // Check if the officer is the one who created this assignment
        if (!planAssignment.getOfficer().getId().equals(officerId)) {
            return ResponseEntity.status(403).build();
        }
        
        // Validate the status parameter
        PlanAssignment.AssignmentStatus assignmentStatus;
        try {
            assignmentStatus = PlanAssignment.AssignmentStatus.valueOf(status.toUpperCase());
            // Only allow ENDED or COMPLETED
            if (assignmentStatus != PlanAssignment.AssignmentStatus.ENDED && 
                assignmentStatus != PlanAssignment.AssignmentStatus.COMPLETED) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status. Only ENDED or COMPLETED are allowed."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status value. Use ENDED or COMPLETED."));
        }
        
        // Update the assignment status
        planService.updateAssignmentStatus(planAssignmentId, assignmentStatus);
        
        String message = "Plan assignment " + status.toLowerCase() + " successfully";
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }
}