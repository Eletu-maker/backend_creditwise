package com.creditwise.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlanTaskDto {
    private String id;
    private String planId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String taskStatus;
}