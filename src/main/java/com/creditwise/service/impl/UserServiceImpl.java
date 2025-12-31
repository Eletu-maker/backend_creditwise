package com.creditwise.service.impl;

import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.dto.RegisterOfficerRequest;
import com.creditwise.dto.UserProfile;
import com.creditwise.entity.ClientProfile;
import com.creditwise.entity.OfficerProfile;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.ClientProfileRepository;
import com.creditwise.repository.OfficerProfileRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientProfileRepository clientProfileRepository;
    
    @Autowired
    private OfficerProfileRepository officerProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createClient(RegisterClientRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.CLIENT)
                .status(User.Status.ACTIVE) // Set to ACTIVE during registration
                .isEnabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create corresponding client profile
        ClientProfile clientProfile = ClientProfile.builder()
                .user(savedUser)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .planStatus(ClientProfile.PlanStatus.PENDING) // Set default plan status
                .build();
        
        clientProfileRepository.save(clientProfile);
        
        return savedUser;
    }

    @Override
    @Transactional
    public User createOfficer(RegisterOfficerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.OFFICER)
                .status(User.Status.INACTIVE) // Set to INACTIVE initially
                .isEnabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create corresponding officer profile
        OfficerProfile officerProfile = OfficerProfile.builder()
                .user(savedUser)
                .maxActiveClients(request.getMaxActiveClients())
                .bio(request.getBio())
                .specialization(request.getSpecialization())
                .status(User.Status.INACTIVE) // Set to INACTIVE initially
                .build();
        
        officerProfileRepository.save(officerProfile);

        return savedUser;
    }

    @Override
    public UserProfile getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserProfile profile = new UserProfile();
        profile.setId(user.getId().toString());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        profile.setRole(user.getRole().name());
        profile.setEnabled(user.isEnabled());

        return profile;
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User updateOfficer(UUID officerId, RegisterOfficerRequest request, String updatedBy) {
        User existingUser = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with id: " + officerId));

        // Update user fields
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        existingUser.setUpdatedBy(updatedBy);

        User updatedUser = userRepository.save(existingUser);

        // Update officer profile
        OfficerProfile existingProfile = officerProfileRepository.findByUser(updatedUser)
                .orElseThrow(() -> new RuntimeException("Officer profile not found for user: " + officerId));

        existingProfile.setMaxActiveClients(request.getMaxActiveClients());
        existingProfile.setBio(request.getBio());
        existingProfile.setSpecialization(request.getSpecialization());
        existingProfile.setUpdatedBy(updatedBy);

        officerProfileRepository.save(existingProfile);

        return updatedUser;
    }
    
    @Override
    public User updateOfficerStatus(UUID officerId, User.Status status, String updatedBy) {
        User existingUser = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with id: " + officerId));

        existingUser.setStatus(status);
        existingUser.setUpdatedBy(updatedBy);

        User updatedUser = userRepository.save(existingUser);

        // Update officer profile status as well
        OfficerProfile existingProfile = officerProfileRepository.findByUser(updatedUser)
                .orElseThrow(() -> new RuntimeException("Officer profile not found for user: " + officerId));

        existingProfile.setStatus(status);
        existingProfile.setUpdatedBy(updatedBy);

        officerProfileRepository.save(existingProfile);

        return updatedUser;
    }
    
    @Override
    public void updateStatusOnLogout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Only update to INACTIVE if the user is an officer and currently ACTIVE
        if (user.getRole() == User.Role.OFFICER && user.getStatus() == User.Status.ACTIVE) {
            user.setStatus(User.Status.INACTIVE);
            userRepository.save(user);
            
            // Update officer profile status as well
            OfficerProfile officerProfile = officerProfileRepository.findByUser(user)
                .orElse(null);
            if (officerProfile != null) {
                officerProfile.setStatus(User.Status.INACTIVE);
                officerProfileRepository.save(officerProfile);
            }
        }
    }
}