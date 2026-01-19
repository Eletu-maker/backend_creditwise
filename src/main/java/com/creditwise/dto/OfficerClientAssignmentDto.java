package com.creditwise.dto;

import com.creditwise.entity.OfficerClientAssignment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OfficerClientAssignmentDto {
    private UUID id;
    private UUID officerId;
    private String officerFirstName;
    private String officerLastName;
    private String officerEmail;
    private UUID clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientEmail;
    private OfficerClientAssignment.AssignmentStatus assignmentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static OfficerClientAssignmentDto fromEntity(OfficerClientAssignment assignment) {
        OfficerClientAssignmentDto dto = new OfficerClientAssignmentDto();
        dto.setId(assignment.getId());
        dto.setOfficerId(assignment.getOfficer().getId());
        dto.setOfficerFirstName(assignment.getOfficer().getFirstName());
        dto.setOfficerLastName(assignment.getOfficer().getLastName());
        dto.setOfficerEmail(assignment.getOfficer().getEmail());
        dto.setClientId(assignment.getClient().getId());
        dto.setClientFirstName(assignment.getClient().getFirstName());
        dto.setClientLastName(assignment.getClient().getLastName());
        dto.setClientEmail(assignment.getClient().getEmail());
        dto.setAssignmentStatus(assignment.getAssignmentStatus());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        dto.setCreatedBy(assignment.getCreatedBy());
        dto.setUpdatedBy(assignment.getUpdatedBy());
        return dto;
    }
}