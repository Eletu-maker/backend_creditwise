package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.ContentDto;
import com.creditwise.entity.Content;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Content>> createContent(@Valid @RequestBody ContentDto contentDto, Authentication authentication) {
        // Extract admin user ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID adminId = userDetails.getUserId();
        
        // Set the creator ID automatically
        contentDto.setCreatorId(adminId.toString());
        
        Content content = contentService.createContent(contentDto);
        return ResponseEntity.ok(ApiResponse.success(content, "Content created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentDto>> getContentById(@PathVariable UUID id) {
        Content content = contentService.getContentById(id);
        contentService.incrementViewCount(id); // Increment view count when content is viewed
        ContentDto contentDto = ContentDto.fromEntity(content);
        return ResponseEntity.ok(ApiResponse.success(contentDto, "Content retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Content>>> getContentsByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Content> contents;
        
        if (category != null) {
            contents = contentService.getContentsByCategory(category, pageable);
        } else if (contentType != null) {
            contents = contentService.getContentsByContentType(contentType, pageable);
        } else {
            // If neither category nor contentType is specified, get all content except deleted
            contents = contentService.getContentsByContentType(null, pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(contents, "Contents retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Content>> updateContent(@PathVariable UUID id, @Valid @RequestBody ContentDto contentDto, Authentication authentication) {
        // Extract admin user ID from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID adminId = userDetails.getUserId();
        
        // Set the creator ID automatically
        contentDto.setCreatorId(adminId.toString());
        
        Content content = contentService.updateContent(id, contentDto);
        return ResponseEntity.ok(ApiResponse.success(content, "Content updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteContent(@PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully", "Content deleted successfully"));
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<String>> likeContent(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        // Check if content is already liked by user
        boolean isAlreadyLiked = contentService.isContentLikedByUser(id, userId.toString());
        
        if (isAlreadyLiked) {
            // If already liked, unlike it
            contentService.unlikeContent(id, userId.toString());
            return ResponseEntity.ok(ApiResponse.success("Content unliked successfully", "Content unliked successfully"));
        } else {
            // If not liked, like it
            contentService.likeContent(id, userId.toString());
            return ResponseEntity.ok(ApiResponse.success("Content liked successfully", "Content liked successfully"));
        }
    }
    
    @PostMapping("/{id}/dislike")
    public ResponseEntity<ApiResponse<String>> dislikeContent(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        // Check if content is already disliked by user
        boolean isAlreadyDisliked = contentService.isContentDislikedByUser(id, userId.toString());
        
        if (isAlreadyDisliked) {
            // If already disliked, undislike it
            contentService.undislikeContent(id, userId.toString());
            return ResponseEntity.ok(ApiResponse.success("Content undisliked successfully", "Content undisliked successfully"));
        } else {
            // If not disliked, dislike it
            contentService.dislikeContent(id, userId.toString());
            return ResponseEntity.ok(ApiResponse.success("Content disliked successfully", "Content disliked successfully"));
        }
    }
    
    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<String>> unlikeContent(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        contentService.unlikeContent(id, userId.toString());
        return ResponseEntity.ok(ApiResponse.success("Content unliked successfully", "Content unliked successfully"));
    }
    
    @DeleteMapping("/{id}/dislike")
    public ResponseEntity<ApiResponse<String>> undislikeContent(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        contentService.undislikeContent(id, userId.toString());
        return ResponseEntity.ok(ApiResponse.success("Content undisliked successfully", "Content undisliked successfully"));
    }
    
    @GetMapping("/{id}/like-status")
    public ResponseEntity<ApiResponse<Boolean>> isContentLiked(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        boolean isLiked = contentService.isContentLikedByUser(id, userId.toString());
        return ResponseEntity.ok(ApiResponse.success(isLiked, "Like status retrieved successfully"));
    }
    
    @GetMapping("/{id}/dislike-status")
    public ResponseEntity<ApiResponse<Boolean>> isContentDisliked(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        boolean isDisliked = contentService.isContentDislikedByUser(id, userId.toString());
        return ResponseEntity.ok(ApiResponse.success(isDisliked, "Dislike status retrieved successfully"));
    }
    
    @GetMapping("/{id}/like-count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(@PathVariable UUID id) {
        Long likeCount = contentService.getLikeCount(id);
        return ResponseEntity.ok(ApiResponse.success(likeCount, "Like count retrieved successfully"));
    }
}