package com.creditwise.service;

import com.creditwise.dto.ApiResponse;
import com.creditwise.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment createComment(UUID contentId, String commentText, String userId);
    List<Comment> getCommentsByContent(UUID contentId);
    Comment updateComment(UUID commentId, String commentText, String userId);
    void deleteComment(UUID commentId, String userId);
    Page<Comment> getCommentsByContentPaginated(UUID contentId, Pageable pageable);
}