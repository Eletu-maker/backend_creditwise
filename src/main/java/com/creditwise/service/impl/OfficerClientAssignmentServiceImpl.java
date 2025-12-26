package com.creditwise.service.impl;

import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.User;
import com.creditwise.exception.OfficerCapacityExceededException;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.OfficerClientAssignmentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.OfficerClientAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OfficerClientAssignmentServiceImpl implements OfficerClientAssignmentService {

    @Autowired
    private OfficerClientAssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OfficerClientAssignment assignOfficerToClient(UUID officerId, UUID clientId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        // Check if client is already assigned to an officer
        if (isClientAlreadyAssigned(clientId)) {
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        // Check if officer has reached capacity
        int activeAssignments = getActiveAssignmentCountForOfficer(officerId);
        // For now, using a default capacity of 10, in a real system this would be in OfficerProfile
        int maxCapacity = 10; 
        if (activeAssignments >= maxCapacity) {
            throw new OfficerCapacityExceededException("Officer has reached maximum active client capacity of " + maxCapacity);
        }

        OfficerClientAssignment assignment = OfficerClientAssignment.builder()
                .officer(officer)
                .client(client)
                .assignmentStatus(OfficerClientAssignment.AssignmentStatus.ACTIVE)
                .build();

        return assignmentRepository.save(assignment);
    }

    @Override
    public List<OfficerClientAssignment> getActiveAssignmentsForOfficer(UUID officerId) {
        return assignmentRepository.findByOfficerIdAndAssignmentStatus(officerId, OfficerClientAssignment.AssignmentStatus.ACTIVE);
    }

    @Override
    public List<OfficerClientAssignment> getAllActiveAssignments() {
        return assignmentRepository.findByAssignmentStatus(OfficerClientAssignment.AssignmentStatus.ACTIVE);
    }

    @Override
    public OfficerClientAssignment getAssignmentById(UUID assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
    }

    @Override
    public OfficerClientAssignment updateAssignmentStatus(UUID assignmentId, OfficerClientAssignment.AssignmentStatus status) {
        OfficerClientAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
        
        assignment.setAssignmentStatus(status);
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    public List<OfficerClientAssignment> findByOfficerIdAndAssignmentStatus(UUID officerId, OfficerClientAssignment.AssignmentStatus status) {
        return assignmentRepository.findByOfficerIdAndAssignmentStatus(officerId, status);
    }

    @Override
    public Optional<OfficerClientAssignment> findByOfficerIdAndClientId(UUID officerId, UUID clientId) {
        return assignmentRepository.findByOfficerIdAndClientId(officerId, clientId);
    }
    
    @Override
    public List<OfficerClientAssignment> getAssignmentsForClient(UUID clientId) {
        return assignmentRepository.findByClientId(clientId);
    }
    
    @Override
    @Transactional
    public void endAssignment(UUID assignmentId) {
        OfficerClientAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
        
        assignment.setAssignmentStatus(OfficerClientAssignment.AssignmentStatus.ENDED);
        assignment.setUpdatedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
    }
    
    @Override
    public int getActiveAssignmentCountForOfficer(UUID officerId) {
        List<OfficerClientAssignment> activeAssignments = 
            assignmentRepository.findByOfficerIdAndAssignmentStatus(officerId, OfficerClientAssignment.AssignmentStatus.ACTIVE);
        return activeAssignments.size();
    }
    
    @Override
    public boolean isClientAlreadyAssigned(UUID clientId) {
        List<OfficerClientAssignment> assignments = assignmentRepository.findByClientId(clientId);
        return assignments.stream()
            .anyMatch(assignment -> assignment.getAssignmentStatus() == OfficerClientAssignment.AssignmentStatus.ACTIVE);
    }
}