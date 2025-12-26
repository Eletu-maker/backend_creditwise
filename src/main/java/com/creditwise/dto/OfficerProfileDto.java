package com.creditwise.dto;

import lombok.Data;

@Data
public class OfficerProfileDto {
    private String id;
    private String userId;
    private Integer maxActiveClients;
    private String bio;
    private String specialization;
}