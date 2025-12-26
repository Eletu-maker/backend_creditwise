package com.creditwise.dto;

import lombok.Data;

@Data
public class CreditPlanDto {
    private String id;
    private String clientId;
    private String officerId;
    private String title;
    private String description;
    private String planStatus;
}