package com.creditwise.dto;

import com.creditwise.entity.CreditPlan;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreditPlanDto {
    private UUID id;
    private String clientId;
    private String officerId;
    private String title;
    private String description;
    private CreditPlan.PlanStatus planStatus;
    private String clientName;
    private String officerName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static CreditPlanDto fromEntity(CreditPlan plan) {
        CreditPlanDto dto = new CreditPlanDto();
        dto.setId(plan.getId());
        dto.setClientId(plan.getClient().getId().toString());
        dto.setOfficerId(plan.getOfficer().getId().toString());
        dto.setTitle(plan.getTitle());
        dto.setDescription(plan.getDescription());
        dto.setPlanStatus(plan.getPlanStatus());
        dto.setClientName(plan.getClient().getFirstName() + " " + plan.getClient().getLastName());
        dto.setOfficerName(plan.getOfficer().getFirstName() + " " + plan.getOfficer().getLastName());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        return dto;
    }
}