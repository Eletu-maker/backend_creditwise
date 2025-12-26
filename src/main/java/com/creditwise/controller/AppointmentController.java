package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.AppointmentDto;
import com.creditwise.entity.Appointment;
import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.AppointmentService;
import com.creditwise.service.OfficerClientAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private OfficerClientAssignmentService officerClientAssignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentDto>> createAppointment(
            @RequestParam(required = false) UUID clientId,
            @RequestParam UUID officerId,
            @RequestParam LocalDateTime appointmentDatetime,
            @RequestParam Appointment.AppointmentType appointmentType,
            @RequestParam String reason,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUserId();
        String currentUserRole = userDetails.getAuthorities().iterator().next().getAuthority();
        
        UUID actualClientId;
        if ("ROLE_CLIENT".equals(currentUserRole)) {
            // If the current user is a client, they can only book appointments for themselves
            actualClientId = currentUserId;
            
            // Check if the officer is assigned to this client
            Optional<OfficerClientAssignment> assignment = officerClientAssignmentService
                    .findByOfficerIdAndClientId(officerId, actualClientId);
            if (assignment.isEmpty() || assignment.get().getAssignmentStatus() != OfficerClientAssignment.AssignmentStatus.ACTIVE) {
                return ResponseEntity.status(403).build();
            }
        } else if ("ROLE_OFFICER".equals(currentUserRole)) {
            // Officers should not be able to book appointments
            return ResponseEntity.status(403).build();
        } else {
            // For admin or other roles, allow specifying the client
            actualClientId = clientId;
            if (actualClientId == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        Appointment appointment = appointmentService.createAppointment(
                actualClientId, officerId, appointmentDatetime, appointmentType, reason);
        
        // Notify the officer about the new appointment
        appointmentService.notifyOfficerOfAppointment(appointment.getId());

        return ResponseEntity.ok(ApiResponse.success(AppointmentDto.fromEntity(appointment)));
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) Appointment.AppointmentStatus status,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUserId();
        String currentUserRole = userDetails.getAuthorities().iterator().next().getAuthority();
        
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        // Check if the current user is authorized to update this appointment
        boolean isAuthorized = false;
        if ("ROLE_CLIENT".equals(currentUserRole)) {
            isAuthorized = appointment.getClient().getId().equals(currentUserId);
        } else if ("ROLE_OFFICER".equals(currentUserRole)) {
            isAuthorized = appointment.getOfficer().getId().equals(currentUserId);
        } else if ("ROLE_ADMIN".equals(currentUserRole)) {
            isAuthorized = true;
        }
        
        if (!isAuthorized) {
            return ResponseEntity.status(403).build();
        }
        
        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, status, notes);
        return ResponseEntity.ok(ApiResponse.success(AppointmentDto.fromEntity(updatedAppointment)));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<String>> cancelAppointment(
            @PathVariable UUID appointmentId,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUserId();
        String currentUserRole = userDetails.getAuthorities().iterator().next().getAuthority();
        
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        // Only officers can cancel appointments
        boolean isAuthorized = "ROLE_OFFICER".equals(currentUserRole) && 
                              appointment.getOfficer().getId().equals(currentUserId);
        
        if (!isAuthorized) {
            return ResponseEntity.status(403).build();
        }
        
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully"));
    }

    @GetMapping("/client")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsForClient(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUserId();
        
        List<Appointment> appointments = appointmentService.getAppointmentsForClient(currentUserId);
        List<AppointmentDto> appointmentDtos = appointments.stream()
                .map(appointment -> AppointmentDto.fromEntity(appointment))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(appointmentDtos));
    }

    @GetMapping("/officer")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAppointmentsForOfficer(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID currentUserId = userDetails.getUserId();
        
        List<Appointment> appointments = appointmentService.getAppointmentsForOfficer(currentUserId);
        List<AppointmentDto> appointmentDtos = appointments.stream()
                .map(appointment -> AppointmentDto.fromEntity(appointment))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(appointmentDtos));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getUpcomingAppointments() {
        // In a real implementation, we would get the user's ID from the security context
        UUID userId = UUID.randomUUID(); // This should be replaced with actual user ID from security context
        List<Appointment> appointments = appointmentService.getUpcomingAppointmentsForUser(userId);
        List<AppointmentDto> appointmentDtos = appointments.stream()
                .map(AppointmentDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(appointmentDtos, "Upcoming appointments retrieved successfully"));
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointmentById(@PathVariable UUID appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        AppointmentDto appointmentDto = AppointmentDto.fromEntity(appointment);
        return ResponseEntity.ok(ApiResponse.success(appointmentDto, "Appointment retrieved successfully"));
    }
}