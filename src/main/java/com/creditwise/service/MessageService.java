package com.creditwise.service;

import com.creditwise.entity.Message;
import com.creditwise.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(UUID senderId, UUID receiverId, String messageText, Message.MessageType messageType);
    List<Message> getConversation(UUID userId1, UUID userId2);
    List<Message> getMessagesByConversationId(String conversationId);
    List<Message> getUnreadMessagesForUser(UUID userId);
    void markMessageAsRead(UUID messageId);
    long getUnreadMessageCount(UUID userId);
}