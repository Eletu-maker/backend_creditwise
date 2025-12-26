package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.ContentDto;
import com.creditwise.entity.Content;
import com.creditwise.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ApiResponse<Content>> createContent(@Valid @RequestBody ContentDto contentDto) {
        Content content = contentService.createContent(contentDto);
        return ResponseEntity.ok(ApiResponse.success(content, "Content created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Content>> getContentById(@PathVariable UUID id) {
        Content content = contentService.getContentById(id);
        contentService.incrementViewCount(id); // Increment view count when content is viewed
        return ResponseEntity.ok(ApiResponse.success(content, "Content retrieved successfully"));
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
            contents = contentService.getContentsByCategory("GENERAL_EDUCATION", pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(contents, "Contents retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Content>> updateContent(@PathVariable UUID id, @Valid @RequestBody ContentDto contentDto) {
        Content content = contentService.updateContent(id, contentDto);
        return ResponseEntity.ok(ApiResponse.success(content, "Content updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteContent(@PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully", "Content deleted successfully"));
    }
}