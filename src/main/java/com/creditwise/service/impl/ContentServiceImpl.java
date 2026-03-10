package com.creditwise.service.impl;

import com.creditwise.dto.ContentDto;
import com.creditwise.entity.*;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.*;
import com.creditwise.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentLikeRepository contentLikeRepository;

    @Autowired
    private UserContentReactionRepository userContentReactionRepository;

    @Override
    public Content createContent(ContentDto contentDto) {
        User creator = userRepository.findById(UUID.fromString(contentDto.getCreatorId()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", contentDto.getCreatorId()));

        Content content;
        try {
            Content.ContentCategory categoryEnum = Content.ContentCategory.valueOf(contentDto.getCategory());
            content = Content.builder()
                    .title(contentDto.getTitle())
                    .body(contentDto.getBody())
                    .contentType(contentDto.getContentType())
                    .category(categoryEnum)
                    .contentCreator(creator)
                    .contentStatus(Content.ContentStatus.ACTIVE) // Set to ACTIVE by default
                    .likeCount(0) // Initialize like count to 0
                    .dislikeCount(0) // Initialize dislike count to 0
                    .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid content category: " + contentDto.getCategory());
        }

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
            try {
                Content.ContentCategory categoryEnum = Content.ContentCategory.valueOf(category);
                return contentRepository.findByCategoryAndContentStatusNot(categoryEnum, Content.ContentStatus.DELETED, pageable);
            } catch (IllegalArgumentException e) {
                // If category is not a valid ContentCategory, return empty page
                return Page.empty(pageable);
            }
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
                Content.ContentCategory categoryEnum = Content.ContentCategory.valueOf(contentType);
                return contentRepository.findByCategoryAndContentStatusNot(categoryEnum, Content.ContentStatus.DELETED, pageable);
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
        try {
            Content.ContentCategory categoryEnum = Content.ContentCategory.valueOf(contentDto.getCategory());
            content.setCategory(categoryEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid content category: " + contentDto.getCategory());
        }
        
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
    
    @Override
    public void likeContent(UUID contentId, String userId) {
        // Fetch content and user
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user already reacted to this content
        Optional<UserContentReaction> existingReactionOpt = userContentReactionRepository.findByContentAndUser(content, user);
        
        if (existingReactionOpt.isPresent()) {
            UserContentReaction existingReaction = existingReactionOpt.get();
            if (existingReaction.getReactionType() == UserContentReaction.ReactionType.LIKE) {
                // Already liked, do nothing
                return;
            } else if (existingReaction.getReactionType() == UserContentReaction.ReactionType.DISLIKE) {
                // User previously disliked, remove the existing reaction first
                userContentReactionRepository.delete(existingReaction);
                
                // Decrement dislike count
                int currentDislikeCount = content.getDislikeCount();
                if (currentDislikeCount > 0) {
                    content.setDislikeCount(currentDislikeCount - 1);
                }
                // Save content after decrementing dislike count
                contentRepository.save(content);
            }
        }

        // Create new like reaction
        UserContentReaction reaction = UserContentReaction.builder()
                .content(content)
                .user(user)
                .reactionType(UserContentReaction.ReactionType.LIKE)
                .build();
        
        userContentReactionRepository.save(reaction);
        
        // Increment like count
        int currentLikeCount = content.getLikeCount();
        content.setLikeCount(currentLikeCount + 1);
        contentRepository.save(content);
    }

    @Override
    public void unlikeContent(UUID contentId, String userId) {
        // Fetch content and user
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user liked this content
        Optional<UserContentReaction> existingReactionOpt = userContentReactionRepository.findByContentAndUser(content, user);
        
        if (existingReactionOpt.isPresent()) {
            UserContentReaction existingReaction = existingReactionOpt.get();
            if (existingReaction.getReactionType() == UserContentReaction.ReactionType.LIKE) {
                // Remove like reaction
                userContentReactionRepository.delete(existingReaction);
                
                // Decrement like count
                int currentLikeCount = content.getLikeCount();
                if (currentLikeCount > 0) {
                    content.setLikeCount(currentLikeCount - 1);
                }
                contentRepository.save(content);
            }
        }
    }

    @Override
    public boolean isContentLikedByUser(UUID contentId, String userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userContentReactionRepository.existsByContentAndUser(content, user)) {
            UserContentReaction reaction = userContentReactionRepository.findByContentAndUser(content, user).orElse(null);
            return reaction != null && reaction.getReactionType() == UserContentReaction.ReactionType.LIKE;
        }
        return false;
    }

    @Override
    public long getLikeCount(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        return content.getLikeCount();
    }
    
    @Override
    public void dislikeContent(UUID contentId, String userId) {
        // Fetch content and user
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user already reacted to this content
        Optional<UserContentReaction> existingReactionOpt = userContentReactionRepository.findByContentAndUser(content, user);
        
        if (existingReactionOpt.isPresent()) {
            UserContentReaction existingReaction = existingReactionOpt.get();
            if (existingReaction.getReactionType() == UserContentReaction.ReactionType.DISLIKE) {
                // Already disliked, do nothing
                return;
            } else if (existingReaction.getReactionType() == UserContentReaction.ReactionType.LIKE) {
                // User previously liked, remove the existing reaction first
                userContentReactionRepository.delete(existingReaction);
                
                // Decrement like count
                int currentLikeCount = content.getLikeCount();
                if (currentLikeCount > 0) {
                    content.setLikeCount(currentLikeCount - 1);
                }
                // Save content after decrementing like count
                contentRepository.save(content);
            }
        }

        // Create new dislike reaction
        UserContentReaction reaction = UserContentReaction.builder()
                .content(content)
                .user(user)
                .reactionType(UserContentReaction.ReactionType.DISLIKE)
                .build();
        
        userContentReactionRepository.save(reaction);
        
        // Increment dislike count
        int currentDislikeCount = content.getDislikeCount();
        content.setDislikeCount(currentDislikeCount + 1);
        contentRepository.save(content);
    }

    @Override
    public void undislikeContent(UUID contentId, String userId) {
        // Fetch content and user
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user disliked this content
        Optional<UserContentReaction> existingReactionOpt = userContentReactionRepository.findByContentAndUser(content, user);
        
        if (existingReactionOpt.isPresent()) {
            UserContentReaction existingReaction = existingReactionOpt.get();
            if (existingReaction.getReactionType() == UserContentReaction.ReactionType.DISLIKE) {
                // Remove dislike reaction
                userContentReactionRepository.delete(existingReaction);
                
                // Decrement dislike count
                int currentDislikeCount = content.getDislikeCount();
                if (currentDislikeCount > 0) {
                    content.setDislikeCount(currentDislikeCount - 1);
                }
                contentRepository.save(content);
            }
        }
    }

    @Override
    public boolean isContentDislikedByUser(UUID contentId, String userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userContentReactionRepository.existsByContentAndUser(content, user)) {
            UserContentReaction reaction = userContentReactionRepository.findByContentAndUser(content, user).orElse(null);
            return reaction != null && reaction.getReactionType() == UserContentReaction.ReactionType.DISLIKE;
        }
        return false;
    }

    @Override
    public long getDislikeCount(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));
        
        return content.getDislikeCount();
    }
}