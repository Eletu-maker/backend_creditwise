package com.creditwise.config;

import com.creditwise.dto.MessageDto;
import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.service.MessageService;
import com.creditwise.service.AssignmentValidationService;
import com.creditwise.exception.UnauthorizedException;
import com.creditwise.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class WebSocketMessageHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private AssignmentValidationService assignmentValidationService;

    @MessageMapping("/chat/{receiverId}")
    public void sendMessage(@DestinationVariable UUID receiverId, 
                           @Payload MessageDto messageDto, 
                           Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User sender = userDetails.getUser();
        
        // Validate that sender and receiver are assigned to each other
        try {
            assignmentValidationService.validateMessagePermission(sender.getId(), receiverId);
        } catch (UnauthorizedException e) {
            // Send error message back to sender
            MessageDto errorDto = new MessageDto();
            errorDto.setMessageText("Error: " + e.getMessage());
            errorDto.setSenderId(receiverId); // Set receiver as sender for error message
            errorDto.setReceiverId(sender.getId());
            messagingTemplate.convertAndSendToUser(
                    sender.getId().toString(), 
                    "/queue/messages", 
                    errorDto
            );
            return;
        }
        
        // Save the message to the database
        Message savedMessage = messageService.sendMessage(
                sender.getId(), 
                receiverId, 
                messageDto.getMessageText(), 
                Message.MessageType.TEXT
        );
        
        // Convert to DTO
        MessageDto responseDto = MessageDto.fromEntity(savedMessage);
        
        // Send the message to the receiver
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(), 
                "/queue/messages", 
                responseDto
        );
        
        // Send confirmation back to the sender
        messagingTemplate.convertAndSendToUser(
                sender.getId().toString(), 
                "/queue/messages", 
                responseDto
        );
    }
}