package com.creditwise.service;

import com.creditwise.entity.User;
import com.creditwise.exception.UnauthorizedException;
import com.creditwise.repository.OfficerClientAssignmentRepository;
import com.creditwise.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentValidationServiceTest {

    @Mock
    private OfficerClientAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssignmentValidationService assignmentValidationService;

    private User officer;
    private User client;
    private User admin;
    private UUID officerId;
    private UUID clientId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        officerId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        officer = new User();
        officer.setId(officerId);
        officer.setRole(User.Role.OFFICER);

        client = new User();
        client.setId(clientId);
        client.setRole(User.Role.CLIENT);

        admin = new User();
        admin.setId(adminId);
        admin.setRole(User.Role.ADMIN);
    }

    @Test
    void validateMessagePermission_OfficerToClient_Assigned_ShouldAllow() {
        // Given
        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                officerId, clientId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE))
                .thenReturn(java.util.Collections.singletonList(new com.creditwise.entity.OfficerClientAssignment()));

        // When & Then
        assertDoesNotThrow(() -> assignmentValidationService.validateMessagePermission(officerId, clientId));
    }

    @Test
    void validateMessagePermission_ClientToOfficer_Assigned_ShouldAllow() {
        // Given
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                officerId, clientId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE))
                .thenReturn(java.util.Collections.singletonList(new com.creditwise.entity.OfficerClientAssignment()));

        // When & Then
        assertDoesNotThrow(() -> assignmentValidationService.validateMessagePermission(clientId, officerId));
    }

    @Test
    void validateMessagePermission_AdminToAnyUser_ShouldAllow() {
        // Given
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));

        // When & Then
        assertDoesNotThrow(() -> assignmentValidationService.validateMessagePermission(adminId, clientId));
    }

    @Test
    void validateMessagePermission_OfficerToClient_NotAssigned_ShouldDeny() {
        // Given
        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                officerId, clientId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE))
                .thenReturn(java.util.Collections.emptyList());

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> assignmentValidationService.validateMessagePermission(officerId, clientId));
        assertEquals("Messages can only be sent between assigned officers and clients", exception.getMessage());
    }

    @Test
    void validateMessagePermission_ClientToClient_ShouldDeny() {
        // Given
        User anotherClient = new User();
        UUID anotherClientId = UUID.randomUUID();
        anotherClient.setId(anotherClientId);
        anotherClient.setRole(User.Role.CLIENT);

        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(anotherClientId)).thenReturn(Optional.of(anotherClient));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
                () -> assignmentValidationService.validateMessagePermission(clientId, anotherClientId));
        assertEquals("Messages can only be sent between assigned officers and clients", exception.getMessage());
    }

    @Test
    void areUsersAssigned_ValidAssignment_ShouldReturnTrue() {
        // Given
        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                officerId, clientId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE))
                .thenReturn(java.util.Collections.singletonList(new com.creditwise.entity.OfficerClientAssignment()));

        // When
        boolean result = assignmentValidationService.areUsersAssigned(officerId, clientId);

        // Then
        assertTrue(result);
    }

    @Test
    void areUsersAssigned_InvalidAssignment_ShouldReturnFalse() {
        // Given
        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                officerId, clientId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE))
                .thenReturn(java.util.Collections.emptyList());

        // When
        boolean result = assignmentValidationService.areUsersAssigned(officerId, clientId);

        // Then
        assertFalse(result);
    }
}