package com.creditwise.service.impl;

import com.creditwise.dto.ContentDto;
import com.creditwise.entity.Content;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.ContentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Content createContent(ContentDto contentDto) {
        User creator = userRepository.findById(UUID.fromString(contentDto.getCreatorId()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", contentDto.getCreatorId()));

        Content content = Content.builder()
                .title(contentDto.getTitle())
                .body(contentDto.getBody())
                .contentType(contentDto.getContentType())
                .category(Content.ContentCategory.valueOf(contentDto.getCategory()))
                .contentCreator(creator)
                .contentStatus(Content.ContentStatus.ACTIVE) // Set to ACTIVE by default
                .build();

        return contentRepository.save(content);
    }

    @Override
    public Content getContentById(UUID contentId) {
        // Use the repository method that fetches the content creator eagerly
        Content content = contentRepository.findByIdWithEagerFetch(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        // Check if content is deleted
        if (content.getContentStatus() == Content.ContentStatus.DELETED) {
            throw new ResourceNotFoundException("Content", "id", contentId);
        }
        
        return content;
    }

    @Override
    public Page<Content> getContentsByCategory(String category, Pageable pageable) {
        if (category != null) {
            return contentRepository.findByCategoryAndContentStatusNot(Content.ContentCategory.valueOf(category), Content.ContentStatus.DELETED, pageable);
        } else {
            return contentRepository.findByContentStatusNot(Content.ContentStatus.DELETED, pageable);
        }
    }

    @Override
    public Page<Content> getContentsByContentType(String contentType, Pageable pageable) {
        // For backward compatibility, we'll treat contentType as category for now
        // If you need to handle content type differently, you can modify this
        try {
            if (contentType != null) {
                return contentRepository.findByCategoryAndContentStatusNot(Content.ContentCategory.valueOf(contentType), Content.ContentStatus.DELETED, pageable);
            } else {
                return contentRepository.findByContentStatusNot(Content.ContentStatus.DELETED, pageable);
            }
        } catch (IllegalArgumentException e) {
            // If contentType is not a valid ContentCategory, return empty page
            return Page.empty(pageable);
        }
    }

    @Override
    public Content updateContent(UUID contentId, ContentDto contentDto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        // Check if content is deleted
        if (content.getContentStatus() == Content.ContentStatus.DELETED) {
            throw new ResourceNotFoundException("Content", "id", contentId);
        }
        
        content.setTitle(contentDto.getTitle());
        content.setBody(contentDto.getBody());
        content.setContentType(contentDto.getContentType());
        content.setCategory(Content.ContentCategory.valueOf(contentDto.getCategory()));
        
        return contentRepository.save(content);
    }

    @Override
    public void deleteContent(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        // Instead of hard delete, set content status to DELETED (soft delete)
        content.setContentStatus(Content.ContentStatus.DELETED);
        contentRepository.save(content);
    }

    @Override
    public void incrementViewCount(UUID contentId) {
        // This method might need to be updated to check content status as well
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        if (content.getContentStatus() == Content.ContentStatus.DELETED) {
            throw new ResourceNotFoundException("Content", "id", contentId);
        }
        
        // In a real implementation, you would increment the view count
        // For now, we just check that the content exists and is not deleted
    }
}