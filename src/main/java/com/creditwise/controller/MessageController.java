package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.MessageDto;
import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.service.MessageService;
import com.creditwise.service.AssignmentValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('CLIENT') or hasRole('OFFICER')")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private AssignmentValidationService assignmentValidationService;

    // REST endpoint to get conversation history
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getConversation(
            Authentication authentication,
            @PathVariable UUID otherUserId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        List<Message> messages = messageService.getConversation(currentUser.getId(), otherUserId);
        List<MessageDto> messageDtos = messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(messageDtos, "Conversation retrieved successfully"));
    }

    // REST endpoint to get unread messages
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getUnreadMessages(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Message> messages = messageService.getUnreadMessagesForUser(user.getId());
        List<MessageDto> messageDtos = messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(messageDtos, "Unread messages retrieved successfully"));
    }

    // REST endpoint to get unread message count
    @GetMapping("/count/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        long count = messageService.getUnreadMessageCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count, "Unread message count retrieved successfully"));
    }

    // REST endpoint to mark message as read
    @PutMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<String>> markMessageAsRead(@PathVariable UUID messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", "Message marked as read successfully"));
    }
    
    // REST endpoint to check if two users are assigned to each other
    @GetMapping("/assignment/check/{userId1}/{userId2}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> checkUserAssignment(
            @PathVariable UUID userId1,
            @PathVariable UUID userId2) {
        boolean areAssigned = assignmentValidationService.areUsersAssigned(userId1, userId2);
        String message = areAssigned ? "Users are assigned to each other" : "Users are not assigned to each other";
        return ResponseEntity.ok(ApiResponse.success(areAssigned, message));
    }
    
    // REST endpoint for users to check if they can message a specific user
    @GetMapping("/can-message/{otherUserId}")
    public ResponseEntity<ApiResponse<Boolean>> canMessageUser(
            Authentication authentication,
            @PathVariable UUID otherUserId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        boolean canMessage = assignmentValidationService.areUsersAssigned(currentUser.getId(), otherUserId);
        String message = canMessage ? "You can message this user" : "You cannot message this user - not assigned";
        return ResponseEntity.ok(ApiResponse.success(canMessage, message));
    }
}