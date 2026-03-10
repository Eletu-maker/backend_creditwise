package com.creditwise.service.impl;

import com.creditwise.entity.Comment;
import com.creditwise.entity.Content;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.CommentRepository;
import com.creditwise.repository.ContentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Comment createComment(UUID contentId, String commentText, String userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .commentText(commentText)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByContent(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));

        return commentRepository.findByContentOrderByCreatedAtDesc(content);
    }

    @Override
    public Comment updateComment(UUID commentId, String commentText, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId.toString()));

        // Check if the user is the owner of the comment
        if (!comment.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only update your own comments");
        }

        comment.setCommentText(commentText);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId.toString()));

        // Check if the user is the owner of the comment
        if (!comment.getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Page<Comment> getCommentsByContentPaginated(UUID contentId, Pageable pageable) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId.toString()));

        return commentRepository.findAllByContentId(contentId, pageable);
    }
}