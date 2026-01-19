package com.creditwise.dto;

import com.creditwise.entity.Plan;
import lombok.Data;

@Data
public class PlanDto {
    private String title;
    private String description;
    private Integer durationInDays;
    private Plan.PlanStatus planStatus;
    private String clientId;
}