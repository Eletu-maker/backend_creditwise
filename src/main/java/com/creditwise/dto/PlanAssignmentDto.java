package com.creditwise.dto;

import com.creditwise.entity.PlanAssignment;
import lombok.Data;

import java.util.UUID;

@Data
public class PlanAssignmentDto {
    private UUID planId;
    private UUID clientId;
    private PlanAssignment.AssignmentStatus assignmentStatus;
}