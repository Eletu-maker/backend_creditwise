package com.creditwise.repository;

import com.creditwise.entity.PlanAssignment;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanAssignmentRepository extends JpaRepository<PlanAssignment, UUID> {
    List<PlanAssignment> findByClientId(UUID clientId);
    List<PlanAssignment> findByOfficerId(UUID officerId);
    List<PlanAssignment> findByClientIdAndAssignmentStatus(UUID clientId, PlanAssignment.AssignmentStatus status);
    Optional<PlanAssignment> findTopByClientIdOrderByCreatedAtDesc(UUID clientId);
    Optional<PlanAssignment> findTopByClientIdAndAssignmentStatusNotOrderByCreatedAtDesc(UUID clientId, PlanAssignment.AssignmentStatus status);
}