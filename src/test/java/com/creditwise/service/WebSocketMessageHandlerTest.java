package com.creditwise.service;

import com.creditwise.config.WebSocketMessageHandler;
import com.creditwise.dto.MessageDto;
import com.creditwise.entity.Message;
import com.creditwise.entity.User;
import com.creditwise.exception.UnauthorizedException;
import com.creditwise.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class WebSocketMessageHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private AssignmentValidationService assignmentValidationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WebSocketMessageHandler webSocketMessageHandler;

    private User sender;
    private CustomUserDetails userDetails;
    private UUID receiverId;
    private MessageDto messageDto;
    private Message savedMessage;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(UUID.randomUUID());
        sender.setRole(User.Role.OFFICER);

        userDetails = new CustomUserDetails(sender);
        receiverId = UUID.randomUUID();

        messageDto = new MessageDto();
        messageDto.setMessageText("Test message");

        User receiver = new User();
        receiver.setId(receiverId);
        receiver.setRole(User.Role.CLIENT);
        
        savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        savedMessage.setSender(sender);
        savedMessage.setReceiver(receiver);
        savedMessage.setMessageText("Test message");
    }

    @Test
    void sendMessage_ValidAssignment_ShouldSendToBothUsers() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(messageService.sendMessage(any(UUID.class), any(UUID.class), anyString(), any(Message.MessageType.class)))
                .thenReturn(savedMessage);

        // When
        webSocketMessageHandler.sendMessage(receiverId, messageDto, authentication);

        // Then
        verify(assignmentValidationService).validateMessagePermission(sender.getId(), receiverId);
        verify(messageService).sendMessage(sender.getId(), receiverId, "Test message", Message.MessageType.TEXT);
        verify(messagingTemplate).convertAndSendToUser(eq(receiverId.toString()), eq("/queue/messages"), any(MessageDto.class));
        verify(messagingTemplate).convertAndSendToUser(eq(sender.getId().toString()), eq("/queue/messages"), any(MessageDto.class));
    }

    @Test
    void sendMessage_InvalidAssignment_ShouldSendErrorToSender() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        doThrow(new UnauthorizedException("Messages can only be sent between assigned officers and clients"))
                .when(assignmentValidationService).validateMessagePermission(sender.getId(), receiverId);

        // When
        webSocketMessageHandler.sendMessage(receiverId, messageDto, authentication);

        // Then
        verify(assignmentValidationService).validateMessagePermission(sender.getId(), receiverId);
        verify(messageService, never()).sendMessage(any(UUID.class), any(UUID.class), anyString(), any(Message.MessageType.class));
        ArgumentCaptor<MessageDto> messageDtoCaptor = ArgumentCaptor.forClass(MessageDto.class);
        verify(messagingTemplate).convertAndSendToUser(eq(sender.getId().toString()), eq("/queue/messages"), messageDtoCaptor.capture());
    }
}