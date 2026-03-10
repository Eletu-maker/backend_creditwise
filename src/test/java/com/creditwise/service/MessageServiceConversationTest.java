package com.creditwise.service;

import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.repository.MessageRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceConversationTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentValidationService assignmentValidationService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User sender;
    private User receiver;
    private UUID senderId;
    private UUID receiverId;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();

        sender = new User();
        sender.setId(senderId);
        sender.setRole(User.Role.OFFICER);

        receiver = new User();
        receiver.setId(receiverId);
        receiver.setRole(User.Role.CLIENT);

        message1 = new Message();
        message1.setId(UUID.randomUUID());
        message1.setSender(sender);
        message1.setReceiver(receiver);
        message1.setMessageText("Hello");
        message1.setConversationId(senderId.toString() + "_" + receiverId.toString());

        message2 = new Message();
        message2.setId(UUID.randomUUID());
        message2.setSender(receiver);
        message2.setReceiver(sender);
        message2.setMessageText("Hi there");
        message2.setConversationId(senderId.toString() + "_" + receiverId.toString());
    }

    @Test
    void getConversation_ShouldReturnMessagesInCorrectOrder() {
        // Given
        String conversationId = senderId.compareTo(receiverId) < 0 
                ? senderId.toString() + "_" + receiverId.toString()
                : receiverId.toString() + "_" + senderId.toString();
        
        List<Message> expectedMessages = Arrays.asList(message1, message2);
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId))
                .thenReturn(expectedMessages);

        // When
        List<Message> result = messageService.getConversation(senderId, receiverId);

        // Then
        assertEquals(2, result.size());
        assertEquals(message1, result.get(0));
        assertEquals(message2, result.get(1));
        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Test
    void getConversation_ReverseOrderIds_ShouldStillWork() {
        // Given
        String conversationId = senderId.compareTo(receiverId) < 0 
                ? senderId.toString() + "_" + receiverId.toString()
                : receiverId.toString() + "_" + senderId.toString();
        
        List<Message> expectedMessages = Arrays.asList(message1);
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId))
                .thenReturn(expectedMessages);

        // When
        List<Message> result = messageService.getConversation(receiverId, senderId);

        // Then
        assertEquals(1, result.size());
        assertEquals(message1, result.get(0));
        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Test
    void getConversation_NoMessages_ShouldReturnEmptyList() {
        // Given
        String conversationId = senderId.compareTo(receiverId) < 0 
                ? senderId.toString() + "_" + receiverId.toString()
                : receiverId.toString() + "_" + senderId.toString();
        
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId))
                .thenReturn(Arrays.asList());

        // When
        List<Message> result = messageService.getConversation(senderId, receiverId);

        // Then
        assertTrue(result.isEmpty());
        verify(messageRepository).findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}