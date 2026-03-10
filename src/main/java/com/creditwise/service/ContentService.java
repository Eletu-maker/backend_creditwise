package com.creditwise.service;

import com.creditwise.dto.ContentDto;
import com.creditwise.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ContentService {
    Content createContent(ContentDto contentDto);
    Content getContentById(UUID contentId);
    Page<Content> getContentsByCategory(String category, Pageable pageable);
    Page<Content> getContentsByContentType(String contentType, Pageable pageable);
    Content updateContent(UUID contentId, ContentDto contentDto);
    void deleteContent(UUID contentId);
    void incrementViewCount(UUID contentId);
    
    // Content engagement methods
    void likeContent(UUID contentId, String userId);
    void unlikeContent(UUID contentId, String userId);
    void dislikeContent(UUID contentId, String userId);
    void undislikeContent(UUID contentId, String userId);
    boolean isContentLikedByUser(UUID contentId, String userId);
    boolean isContentDislikedByUser(UUID contentId, String userId);
    long getLikeCount(UUID contentId);
    long getDislikeCount(UUID contentId);
}