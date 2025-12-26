package com.creditwise.service;

import com.creditwise.entity.OfficerClientAssignment;
import com.creditwise.entity.OfficerProfile;
import com.creditwise.entity.User;
import com.creditwise.exception.OfficerCapacityExceededException;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.OfficerClientAssignmentRepository;
import com.creditwise.repository.OfficerProfileRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.impl.OfficerClientAssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OfficerClientAssignmentServiceTest {

    @Mock
    private OfficerClientAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfficerProfileRepository officerProfileRepository;

    @InjectMocks
    private OfficerClientAssignmentServiceImpl assignmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignOfficerToClient_ShouldCreateAssignment_WhenValidRequest() {
        // Arrange
        UUID officerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        User officer = User.builder().id(officerId).role(User.Role.OFFICER).build();
        User client = User.builder().id(clientId).role(User.Role.CLIENT).build();
        OfficerProfile officerProfile = OfficerProfile.builder().user(officer).maxActiveClients(5).build();

        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(assignmentRepository.countActiveAssignmentsByOfficer(officer)).thenReturn(2);
        when(officerProfileRepository.findByUser(officer)).thenReturn(Optional.of(officerProfile));
        when(assignmentRepository.save(any(OfficerClientAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock the isClientAlreadyAssigned check
        when(assignmentRepository.findByClientAndAssignmentStatus(eq(client), any(OfficerClientAssignment.AssignmentStatus.class)))
                .thenReturn(List.of());

        // Act
        OfficerClientAssignment result = assignmentService.assignOfficerToClient(officerId, clientId);

        // Assert
        assertNotNull(result);
        assertEquals(officer, result.getOfficer());
        assertEquals(client, result.getClient());
        assertEquals(OfficerClientAssignment.AssignmentStatus.ACTIVE, result.getAssignmentStatus());
        
        // Verify calls - note that findById is called twice for client (once in assignOfficerToClient, once in isClientAlreadyAssigned)
        verify(userRepository, times(1)).findById(officerId);
        verify(userRepository, times(2)).findById(clientId); // Called twice for client
        verify(assignmentRepository).countActiveAssignmentsByOfficer(officer);
        verify(officerProfileRepository).findByUser(officer);
        verify(assignmentRepository).save(any(OfficerClientAssignment.class));
    }

    @Test
    void assignOfficerToClient_ShouldThrowException_WhenOfficerNotFound() {
        // Arrange
        UUID officerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(userRepository.findById(officerId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assignmentService.assignOfficerToClient(officerId, clientId);
        });

        assertTrue(exception.getMessage().contains("Officer"));
        verify(userRepository, times(1)).findById(officerId);
        verify(userRepository, never()).findById(clientId);
        verify(assignmentRepository, never()).save(any(OfficerClientAssignment.class));
    }

    @Test
    void assignOfficerToClient_ShouldThrowException_WhenOfficerCapacityExceeded() {
        // Arrange
        UUID officerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        User officer = User.builder().id(officerId).role(User.Role.OFFICER).build();
        User client = User.builder().id(clientId).role(User.Role.CLIENT).build();
        OfficerProfile officerProfile = OfficerProfile.builder().user(officer).maxActiveClients(2).build();

        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(assignmentRepository.countActiveAssignmentsByOfficer(officer)).thenReturn(2);
        when(officerProfileRepository.findByUser(officer)).thenReturn(Optional.of(officerProfile));
        
        // Mock the isClientAlreadyAssigned check
        when(assignmentRepository.findByClientAndAssignmentStatus(eq(client), any(OfficerClientAssignment.AssignmentStatus.class)))
                .thenReturn(List.of());

        // Act & Assert
        OfficerCapacityExceededException exception = assertThrows(OfficerCapacityExceededException.class, () -> {
            assignmentService.assignOfficerToClient(officerId, clientId);
        });

        assertTrue(exception.getMessage().contains("capacity"));
        verify(userRepository, times(1)).findById(officerId);
        verify(userRepository, times(2)).findById(clientId); // Called twice for client
        verify(assignmentRepository).countActiveAssignmentsByOfficer(officer);
        verify(officerProfileRepository).findByUser(officer);
        verify(assignmentRepository, never()).save(any(OfficerClientAssignment.class));
    }

    @Test
    void getActiveAssignmentsForOfficer_ShouldReturnAssignments_WhenOfficerExists() {
        // Arrange
        UUID officerId = UUID.randomUUID();
        User officer = User.builder().id(officerId).role(User.Role.OFFICER).build();
        List<OfficerClientAssignment> assignments = List.of(mock(OfficerClientAssignment.class));

        when(userRepository.findById(officerId)).thenReturn(Optional.of(officer));
        when(assignmentRepository.findByOfficerAndAssignmentStatus(eq(officer), any(OfficerClientAssignment.AssignmentStatus.class)))
                .thenReturn(assignments);

        // Act
        List<OfficerClientAssignment> result = assignmentService.getActiveAssignmentsForOfficer(officerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findById(officerId);
        verify(assignmentRepository).findByOfficerAndAssignmentStatus(eq(officer), any(OfficerClientAssignment.AssignmentStatus.class));
    }
}