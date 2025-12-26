package com.creditwise.service.impl;

import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.repository.MessageRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.MessageService;
import com.creditwise.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Message sendMessage(UUID senderId, UUID receiverId, String messageText, Message.MessageType messageType) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));

        // Generate conversation ID based on the two users' IDs (smaller ID first to ensure consistency)
        String conversationId = senderId.compareTo(receiverId) < 0 
                ? senderId.toString() + "_" + receiverId.toString()
                : receiverId.toString() + "_" + senderId.toString();

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .conversationId(conversationId)
                .messageText(messageText)
                .messageType(messageType)
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(UUID userId1, UUID userId2) {
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
                userId1, userId2, userId1, userId2);
    }

    @Override
    public List<Message> getMessagesByConversationId(String conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Override
    public List<Message> getUnreadMessagesForUser(UUID userId) {
        return messageRepository.findUnreadMessagesByReceiverId(userId);
    }

    @Override
    public void markMessageAsRead(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    public long getUnreadMessageCount(UUID userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}