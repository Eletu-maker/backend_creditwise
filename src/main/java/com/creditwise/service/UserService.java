package com.creditwise.service;

import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.dto.UserProfile;
import com.creditwise.entity.User;

import java.util.UUID;

public interface UserService {
    User createUser(RegisterClientRequest request);
    UserProfile getUserProfile(UUID userId);
    User getUserById(UUID userId);
    User getUserByEmail(String email);
    boolean existsByEmail(String email);
}