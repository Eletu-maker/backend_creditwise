package com.creditwise.service;

import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfficerClientAssignmentService {
    OfficerClientAssignment assignOfficerToClient(UUID officerId, UUID clientId);
    List<OfficerClientAssignment> getActiveAssignmentsForOfficer(UUID officerId);
    List<OfficerClientAssignment> getAllActiveAssignments();
    OfficerClientAssignment getAssignmentById(UUID assignmentId);
    OfficerClientAssignment updateAssignmentStatus(UUID assignmentId, OfficerClientAssignment.AssignmentStatus status);
    
    // Additional methods needed for the OfficerController
    List<OfficerClientAssignment> findByOfficerIdAndAssignmentStatus(UUID officerId, OfficerClientAssignment.AssignmentStatus status);
    Optional<OfficerClientAssignment> findByOfficerIdAndClientId(UUID officerId, UUID clientId);
    
    // Methods that were already implemented in the service
    List<OfficerClientAssignment> getAssignmentsForClient(UUID clientId);
    void endAssignment(UUID assignmentId);
    int getActiveAssignmentCountForOfficer(UUID officerId);
    boolean isClientAlreadyAssigned(UUID clientId);
}