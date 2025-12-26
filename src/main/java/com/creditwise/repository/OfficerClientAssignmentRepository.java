package com.creditwise.repository;

import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficerClientAssignmentRepository extends JpaRepository<OfficerClientAssignment, UUID> {
    List<OfficerClientAssignment> findByOfficerAndAssignmentStatus(User officer, OfficerClientAssignment.AssignmentStatus status);
    List<OfficerClientAssignment> findByClientAndAssignmentStatus(User client, OfficerClientAssignment.AssignmentStatus status);
    List<OfficerClientAssignment> findByAssignmentStatus(OfficerClientAssignment.AssignmentStatus status);
    Optional<OfficerClientAssignment> findByOfficerAndClientAndAssignmentStatus(User officer, User client, OfficerClientAssignment.AssignmentStatus status);
    
    @Query("SELECT COUNT(o) FROM OfficerClientAssignment o WHERE o.officer = :officer AND o.assignmentStatus = 'ACTIVE'")
    int countActiveAssignmentsByOfficer(User officer);
    
    List<OfficerClientAssignment> findByOfficerIdAndAssignmentStatus(UUID officerId, OfficerClientAssignment.AssignmentStatus status);
    
    @Query("SELECT oca FROM OfficerClientAssignment oca WHERE oca.officer.id = :officerId AND oca.client.id = :clientId")
    Optional<OfficerClientAssignment> findByOfficerIdAndClientId(UUID officerId, UUID clientId);
    
    List<OfficerClientAssignment> findByClientId(UUID clientId);
    
    @Modifying
    @Query("UPDATE OfficerClientAssignment o SET o.assignmentStatus = :status WHERE o.id = :assignmentId")
    void updateAssignmentStatusById(UUID assignmentId, OfficerClientAssignment.AssignmentStatus status);
}