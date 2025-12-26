package com.creditwise.dto;

import lombok.Data;

@Data
public class ContentDto {
    private String id;
    private String title;
    private String body;
    private String contentType;
    private String category;
    private Integer viewCount;
    private String creatorId;
}