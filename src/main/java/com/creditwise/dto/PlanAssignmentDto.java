package com.creditwise.dto;

import com.creditwise.entity.PlanAssignment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PlanAssignmentDto {
    private UUID id;
    private UUID clientId;
    private UUID officerId;
    private UUID planId;
    private String planTitle;
    private String clientName;
    private String officerName;
    private PlanAssignment.AssignmentStatus assignmentStatus;
    private Integer progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlanAssignmentDto fromEntity(PlanAssignment assignment) {
        PlanAssignmentDto dto = new PlanAssignmentDto();
        dto.setId(assignment.getId());
        dto.setClientId(assignment.getClient().getId());
        dto.setOfficerId(assignment.getOfficer().getId());
        dto.setPlanId(assignment.getPlan().getId());
        dto.setPlanTitle(assignment.getPlan().getTitle());
        dto.setClientName(assignment.getClient().getFirstName() + " " + assignment.getClient().getLastName());
        dto.setOfficerName(assignment.getOfficer().getFirstName() + " " + assignment.getOfficer().getLastName());
        dto.setAssignmentStatus(assignment.getAssignmentStatus());
        dto.setProgressPercentage(assignment.getProgressPercentage());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }
}