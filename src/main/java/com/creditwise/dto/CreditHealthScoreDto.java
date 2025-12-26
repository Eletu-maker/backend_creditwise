package com.creditwise.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditHealthScoreDto {
    private String id;
    private String clientId;  // This should be UUID in the future, but keeping as string for now
    private LocalDate scoreDate;
    private Integer score;  // This is the main score field
    private Integer paymentHistoryScore;
    private Integer creditUtilizationScore;
    private Integer creditAgeScore;
    private Integer creditMixScore;
    private Integer recentActivityScore;
    private String disclaimer;
}