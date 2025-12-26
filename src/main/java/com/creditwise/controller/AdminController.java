package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.User;
import com.creditwise.service.OfficerClientAssignmentService;
import com.creditwise.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OfficerClientAssignmentService assignmentService;

    @PostMapping("/officers")
    public ResponseEntity<ApiResponse<User>> createOfficer(@Valid @RequestBody RegisterClientRequest registerRequest) {
        // In a real implementation, we would set the role to OFFICER
        // For now, we'll use the existing client registration method but modify the role
        User user = userService.createUser(registerRequest);
        // We would need to modify the createUser method to accept a role parameter
        return ResponseEntity.ok(ApiResponse.success(user, "Officer created successfully"));
    }

    @PostMapping("/assign-officer-to-client")
    public ResponseEntity<ApiResponse<OfficerClientAssignment>> assignOfficerToClient(
            @RequestParam UUID officerId,
            @RequestParam UUID clientId) {
        OfficerClientAssignment assignment = assignmentService.assignOfficerToClient(officerId, clientId);
        return ResponseEntity.ok(ApiResponse.success(assignment, "Officer assigned to client successfully"));
    }

    @GetMapping("/officer-client-assignments")
    public ResponseEntity<ApiResponse<List<OfficerClientAssignment>>> getAllAssignments() {
        List<OfficerClientAssignment> assignments = assignmentService.getAllActiveAssignments();
        return ResponseEntity.ok(ApiResponse.success(assignments, "All assignments retrieved successfully"));
    }

    @GetMapping("/officer-client-assignments/{assignmentId}")
    public ResponseEntity<ApiResponse<OfficerClientAssignment>> getAssignmentById(@PathVariable UUID assignmentId) {
        OfficerClientAssignment assignment = assignmentService.getAssignmentById(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(assignment, "Assignment retrieved successfully"));
    }

    @DeleteMapping("/officer-client-assignments/{assignmentId}")
    public ResponseEntity<ApiResponse<String>> removeAssignment(@PathVariable UUID assignmentId) {
        assignmentService.endAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Assignment removed successfully", "Assignment removed successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<String>> getPlatformStatistics() {
        // Placeholder for platform statistics
        String stats = "Placeholder for platform statistics";
        return ResponseEntity.ok(ApiResponse.success(stats, "Platform statistics retrieved successfully"));
    }
}