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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Content createContent(ContentDto contentDto) {
        User creator = userRepository.findById(UUID.fromString(contentDto.getCreatorId()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", contentDto.getCreatorId()));

        Content content = Content.builder()
                .contentCreator(creator)
                .title(contentDto.getTitle())
                .body(contentDto.getBody())
                .contentType(Content.ContentType.valueOf(contentDto.getContentType()))
                .category(contentDto.getCategory() != null ? Content.ContentCategory.valueOf(contentDto.getCategory()) : null)
                .viewCount(0)
                .build();

        return contentRepository.save(content);
    }

    @Override
    @Transactional(readOnly = true)
    public Content getContentById(UUID contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Content> getContentsByCategory(String category, Pageable pageable) {
        Content.ContentCategory contentCategory = Content.ContentCategory.valueOf(category);
        return contentRepository.findByCategory(contentCategory, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Content> getContentsByContentType(String contentType, Pageable pageable) {
        Content.ContentType contentContentType = Content.ContentType.valueOf(contentType);
        return contentRepository.findByContentType(contentContentType, pageable);
    }

    @Override
    @Transactional
    public Content updateContent(UUID contentId, ContentDto contentDto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        content.setTitle(contentDto.getTitle());
        content.setBody(contentDto.getBody());
        content.setContentType(Content.ContentType.valueOf(contentDto.getContentType()));
        
        if (contentDto.getCategory() != null) {
            content.setCategory(Content.ContentCategory.valueOf(contentDto.getCategory()));
        }

        return contentRepository.save(content);
    }

    @Override
    @Transactional
    public void deleteContent(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        contentRepository.delete(content);
    }

    @Override
    @Transactional
    public void incrementViewCount(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        content.setViewCount(content.getViewCount() + 1);
        contentRepository.save(content);
    }
}