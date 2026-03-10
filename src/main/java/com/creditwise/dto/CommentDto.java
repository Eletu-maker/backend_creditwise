package com.creditwise.dto;

import com.creditwise.entity.Comment;
import com.creditwise.entity.Content;
import com.creditwise.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private UUID id;
    private String commentText;
    private UUID contentId;
    private UUID userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentDto fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        User user = comment.getUser();
        Content content = comment.getContent();
        
        return CommentDto.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .contentId(content != null ? content.getId() : null)
                .userId(user != null ? user.getId() : null)
                .userName(user != null ? user.getFirstName() + " " + user.getLastName() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}