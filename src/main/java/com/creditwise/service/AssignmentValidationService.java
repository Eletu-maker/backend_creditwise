package com.creditwise.service;

import com.creditwise.entity.User;
import com.creditwise.repository.OfficerClientAssignmentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AssignmentValidationService {

    @Autowired
    private OfficerClientAssignmentRepository assignmentRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Validates that a message can be sent between two users based on their assignment relationship
     * @param senderId The ID of the user sending the message
     * @param receiverId The ID of the user receiving the message
     * @throws UnauthorizedException if the users are not assigned to each other
     */
    public void validateMessagePermission(UUID senderId, UUID receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UnauthorizedException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UnauthorizedException("Receiver not found"));

        // If either user is an admin, allow the message (admins can communicate with anyone)
        if (sender.getRole() == User.Role.ADMIN || receiver.getRole() == User.Role.ADMIN) {
            return;
        }

        // Check if sender is officer and receiver is client (and they are assigned)
        if (sender.getRole() == User.Role.OFFICER && receiver.getRole() == User.Role.CLIENT) {
            boolean isAssigned = assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                    senderId, receiverId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE)
                    .stream()
                    .findFirst()
                    .isPresent();
            
            if (isAssigned) {
                return; // Valid assignment found
            }
        }
        
        // Check if sender is client and receiver is officer (and they are assigned)
        else if (sender.getRole() == User.Role.CLIENT && receiver.getRole() == User.Role.OFFICER) {
            boolean isAssigned = assignmentRepository.findByOfficerIdAndClientIdAndAssignmentStatus(
                    receiverId, senderId, com.creditwise.entity.OfficerClientAssignment.AssignmentStatus.ACTIVE)
                    .stream()
                    .findFirst()
                    .isPresent();
            
            if (isAssigned) {
                return; // Valid assignment found
            }
        }

        // If we reach here, the users are not properly assigned to each other
        throw new UnauthorizedException("Messages can only be sent between assigned officers and clients");
    }

    /**
     * Checks if two users are assigned to each other
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if users are assigned to each other, false otherwise
     */
    public boolean areUsersAssigned(UUID userId1, UUID userId2) {
        try {
            validateMessagePermission(userId1, userId2);
            return true;
        } catch (UnauthorizedException e) {
            return false;
        }
    }
}