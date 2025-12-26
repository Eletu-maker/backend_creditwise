package com.creditwise.service;

import com.creditwise.dto.ContentDto;
import com.creditwise.entity.Content;
import com.creditwise.entity.User;
import com.creditwise.repository.ContentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.impl.ContentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContentServiceImpl contentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createContent_ShouldCreateContent_WhenValidRequest() {
        // Arrange
        ContentDto contentDto = new ContentDto();
        UUID creatorId = UUID.randomUUID();
        contentDto.setCreatorId(creatorId.toString());
        contentDto.setTitle("Test Content");
        contentDto.setBody("This is test content");
        contentDto.setContentType("ARTICLE");
        contentDto.setCategory("GENERAL_EDUCATION");

        User creator = User.builder().id(creatorId).role(User.Role.OFFICER).build();
        Content content = Content.builder()
                .id(UUID.randomUUID())
                .contentCreator(creator)
                .title("Test Content")
                .body("This is test content")
                .contentType(Content.ContentType.ARTICLE)
                .category(Content.ContentCategory.GENERAL_EDUCATION)
                .viewCount(0)
                .build();

        when(userRepository.findById(creatorId)).thenReturn(Optional.of(creator));
        when(contentRepository.save(any(Content.class))).thenReturn(content);

        // Act
        Content result = contentService.createContent(contentDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Content", result.getTitle());
        assertEquals(creator, result.getContentCreator());
        assertEquals(Content.ContentType.ARTICLE, result.getContentType());
        assertEquals(0, result.getViewCount());
        
        verify(userRepository).findById(creatorId);
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void getContentById_ShouldReturnContent_WhenContentExists() {
        // Arrange
        UUID contentId = UUID.randomUUID();
        Content content = Content.builder()
                .id(contentId)
                .title("Test Content")
                .body("This is test content")
                .contentType(Content.ContentType.ARTICLE)
                .viewCount(0)
                .build();

        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));

        // Act
        Content result = contentService.getContentById(contentId);

        // Assert
        assertNotNull(result);
        assertEquals(contentId, result.getId());
        assertEquals("Test Content", result.getTitle());
        verify(contentRepository).findById(contentId);
    }

    @Test
    void getContentsByCategory_ShouldReturnPageOfContent() {
        // Arrange
        String category = "GENERAL_EDUCATION";
        Pageable pageable = PageRequest.of(0, 10);
        Content content = Content.builder()
                .id(UUID.randomUUID())
                .title("Test Content")
                .body("This is test content")
                .contentType(Content.ContentType.ARTICLE)
                .category(Content.ContentCategory.GENERAL_EDUCATION)
                .viewCount(0)
                .build();
        Page<Content> contentPage = new PageImpl<>(List.of(content));

        when(contentRepository.findByCategory(Content.ContentCategory.GENERAL_EDUCATION, pageable))
                .thenReturn(contentPage);

        // Act
        Page<Content> result = contentService.getContentsByCategory(category, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Content", result.getContent().get(0).getTitle());
        verify(contentRepository).findByCategory(Content.ContentCategory.GENERAL_EDUCATION, pageable);
    }
}