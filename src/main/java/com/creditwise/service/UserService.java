package com.creditwise.service;

import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.dto.RegisterOfficerRequest;
import com.creditwise.dto.UserProfile;
import com.creditwise.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createClient(RegisterClientRequest request);

    User createOfficer(RegisterOfficerRequest request);

    List<User> getAllUsersByRole(User.Role role);

    User updateOfficer(UUID officerId, RegisterOfficerRequest request, String updatedBy);
    
    User updateOfficerStatus(UUID officerId, User.Status status, String updatedBy);
    
    void updateStatusOnLogout(String email);

    UserProfile getUserProfile(UUID userId);

    User getUserById(UUID userId);

    User getUserByEmail(String email);

    boolean existsByEmail(String email);
}