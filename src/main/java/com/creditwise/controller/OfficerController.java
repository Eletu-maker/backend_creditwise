package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.AppointmentDto;
import com.creditwise.entity.Appointment;
import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.ClientProfile;
import com.creditwise.repository.AppointmentRepository;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.AppointmentService;
import com.creditwise.service.OfficerClientAssignmentService;
import com.creditwise.repository.ClientProfileRepository;
import com.creditwise.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<List<OfficerClientAssignment>>> getMyAssignedClients(Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        List<OfficerClientAssignment> assignments = assignmentService.findByOfficerIdAndAssignmentStatus(officerId, OfficerClientAssignment.AssignmentStatus.ACTIVE);
        return ResponseEntity.ok(ApiResponse.success(assignments));
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
        
        return ResponseEntity.ok(ApiResponse.success(appointmentDtos));
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<ApiResponse<OfficerClientAssignment>> getClientById(@PathVariable UUID clientId, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        OfficerClientAssignment assignment = assignmentService.findByOfficerIdAndClientId(officerId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "officerId and clientId", officerId + " and " + clientId));
        return ResponseEntity.ok(ApiResponse.success(assignment));
    }

    @PutMapping("/clients/{clientId}")
    public ResponseEntity<ApiResponse<ClientProfile>> updateClientProfile(@PathVariable UUID clientId, @RequestBody ClientProfile updatedProfile, Authentication authentication) {
        // Extract officer ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID officerId = userDetails.getUserId();
        
        // Check if the officer is assigned to this client
        boolean isAssigned = assignmentService.findByOfficerIdAndClientId(officerId, clientId).isPresent();
        if (!isAssigned) {
            return ResponseEntity.status(403).build();
        }
        
        // Only allow updating the plan status
        ClientProfile existingProfile = clientProfileRepository.findByUserId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientProfile", "userId", clientId));
        
        // Update only the plan status
        existingProfile.setPlanStatus(updatedProfile.getPlanStatus());
        existingProfile = clientProfileRepository.save(existingProfile);
        
        return ResponseEntity.ok(ApiResponse.success(existingProfile));
    }

    @PostMapping("/end-assignment/{assignmentId}")
    public ResponseEntity<ApiResponse<String>> endAssignment(@PathVariable UUID assignmentId) {
        assignmentService.endAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Assignment ended successfully"));
    }
}