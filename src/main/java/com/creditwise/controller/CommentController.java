package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.CommentDto;
import com.creditwise.entity.Comment;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/content/{contentId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @PathVariable UUID contentId,
            @RequestBody @Valid CommentDto commentDto,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        Comment comment = commentService.createComment(contentId, commentDto.getCommentText(), userId.toString());
        CommentDto responseDto = CommentDto.fromEntity(comment);
        
        return ResponseEntity.ok(ApiResponse.success(responseDto, "Comment created successfully"));
    }

    @GetMapping("/content/{contentId}")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getCommentsByContent(
            @PathVariable UUID contentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentsPage = commentService.getCommentsByContentPaginated(contentId, pageable);
        
        List<CommentDto> commentDtos = commentsPage.getContent().stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(commentDtos, "Comments retrieved successfully"));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(
            @PathVariable UUID commentId,
            @RequestBody @Valid CommentDto commentDto,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        Comment updatedComment = commentService.updateComment(commentId, commentDto.getCommentText(), userId.toString());
        CommentDto responseDto = CommentDto.fromEntity(updatedComment);
        
        return ResponseEntity.ok(ApiResponse.success(responseDto, "Comment updated successfully"));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();
        
        commentService.deleteComment(commentId, userId.toString());
        
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", "Comment deleted successfully"));
    }
}