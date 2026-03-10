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
    private Integer likeCount;
    private Integer dislikeCount;
    
    public static ContentDto fromEntity(com.creditwise.entity.Content content) {
        ContentDto dto = new ContentDto();
        dto.setId(content.getId().toString());
        dto.setTitle(content.getTitle());
        dto.setBody(content.getBody());
        dto.setContentType(content.getContentType());
        dto.setCategory(content.getCategory().name());
        dto.setViewCount(0); // Assuming view count is tracked separately
        dto.setCreatorId(content.getContentCreator().getId().toString());
        dto.setLikeCount(content.getLikeCount());
        dto.setDislikeCount(content.getDislikeCount());
        return dto;
    }
}