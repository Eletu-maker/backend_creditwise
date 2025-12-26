package com.creditwise.service.impl;

import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.dto.UserProfile;
import com.creditwise.entity.User;
import com.creditwise.repository.UserRepository;
import com.creditwise.security.JwtUtils;
import com.creditwise.service.AuthService;
import com.creditwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        com.creditwise.security.CustomUserDetails userDetails = (com.creditwise.security.CustomUserDetails) authentication.getPrincipal();
        
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userDetails.getUserId().toString());
        userProfile.setFirstName(userDetails.getUser().getFirstName());
        userProfile.setLastName(userDetails.getUser().getLastName());
        userProfile.setEmail(userDetails.getUser().getEmail());
        userProfile.setRole(userDetails.getUser().getRole().name());
        userProfile.setEnabled(userDetails.getUser().isEnabled());

        return new JwtResponse(jwt, refreshToken, userProfile);
    }

    @Override
    public JwtResponse registerClient(RegisterClientRequest registerRequest) {
        User user = userService.createUser(registerRequest);
        
        // Authenticate the user after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        
        com.creditwise.security.CustomUserDetails userDetails = (com.creditwise.security.CustomUserDetails) authentication.getPrincipal();
        
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userDetails.getUserId().toString());
        userProfile.setFirstName(userDetails.getUser().getFirstName());
        userProfile.setLastName(userDetails.getUser().getLastName());
        userProfile.setEmail(userDetails.getUser().getEmail());
        userProfile.setRole(userDetails.getUser().getRole().name());
        userProfile.setEnabled(userDetails.getUser().isEnabled());
        
        return new JwtResponse(jwt, refreshToken, userProfile);
    }
}