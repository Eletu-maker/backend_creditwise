package com.creditwise.dto;

import com.creditwise.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private UUID receiverId;
    private String receiverName;
    private String conversationId;
    private String messageText;
    private boolean isRead;  // Changed back to boolean to match entity
    private Message.MessageType messageType;
    private LocalDateTime createdAt;
    
    // Additional constructor for WebSocket messages
    public MessageDto(String messageText) {
        this.messageText = messageText;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
    
    public static MessageDto fromEntity(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName());
        dto.setConversationId(message.getConversationId());
        dto.setMessageText(message.getMessageText());
        dto.setIsRead(message.isRead());
        dto.setMessageType(message.getMessageType());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}