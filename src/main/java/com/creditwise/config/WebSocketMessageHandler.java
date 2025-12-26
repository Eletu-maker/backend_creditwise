package com.creditwise.config;

import com.creditwise.dto.MessageDto;
import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.service.MessageService;
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

    @MessageMapping("/chat/{receiverId}")
    public void sendMessage(@DestinationVariable UUID receiverId, 
                           @Payload MessageDto messageDto, 
                           Authentication authentication) {
        User sender = (User) authentication.getPrincipal();
        
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