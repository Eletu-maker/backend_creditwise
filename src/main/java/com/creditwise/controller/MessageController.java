package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.MessageDto;
import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.service.MessageService;
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

    // REST endpoint to get conversation history
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getConversation(
            Authentication authentication,
            @PathVariable UUID otherUserId) {
        User currentUser = (User) authentication.getPrincipal();
        List<Message> messages = messageService.getConversation(currentUser.getId(), otherUserId);
        List<MessageDto> messageDtos = messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(messageDtos, "Conversation retrieved successfully"));
    }

    // REST endpoint to get unread messages
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getUnreadMessages(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Message> messages = messageService.getUnreadMessagesForUser(user.getId());
        List<MessageDto> messageDtos = messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(messageDtos, "Unread messages retrieved successfully"));
    }

    // REST endpoint to get unread message count
    @GetMapping("/count/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        long count = messageService.getUnreadMessageCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count, "Unread message count retrieved successfully"));
    }

    // REST endpoint to mark message as read
    @PutMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<String>> markMessageAsRead(@PathVariable UUID messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", "Message marked as read successfully"));
    }
}