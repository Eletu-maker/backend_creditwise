package com.creditwise.service.impl;

import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.dto.UserProfile;
import com.creditwise.entity.ClientProfile;
import com.creditwise.entity.OfficerProfile;
import com.creditwise.entity.User;
import com.creditwise.repository.ClientProfileRepository;
import com.creditwise.repository.OfficerProfileRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.security.CustomUserDetails;
import com.creditwise.security.JwtUtils;
import com.creditwise.service.AuthService;
import com.creditwise.service.OtpAuthService;
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
    private ClientProfileRepository clientProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private OtpAuthService otpAuthService;
    
    @Autowired
    private OfficerProfileRepository officerProfileRepository;
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // Check if the user is deleted
        if (userDetails.getUser().getStatus() == User.Status.DELETED) {
            throw new RuntimeException("Account has been deleted and cannot be accessed");
        }
        
        // Update status to ACTIVE when user logs in (for both officers and clients)
        if (userDetails.getUser().getStatus() != User.Status.ACTIVE) {
            User user = userDetails.getUser();
            user.setStatus(User.Status.ACTIVE);
            userRepository.save(user);
            
            // Update officer profile status as well if it's an officer
            if (user.getRole() == User.Role.OFFICER) {
                OfficerProfile officerProfile = officerProfileRepository.findByUser(user)
                        .orElseThrow(() -> new RuntimeException("Officer profile not found for user: " + user.getId()));
                officerProfile.setStatus(User.Status.ACTIVE);
                officerProfileRepository.save(officerProfile);
            }
        }

        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        
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
        User user = userService.createClient(registerRequest);
        
        // Authenticate the user after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
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
    public JwtResponse authenticateAdminWithOtp(String email, String otpCode) {
        // Verify the OTP first
        boolean isValidOtp = otpAuthService.verifyOtp(email, otpCode);
        
        if (!isValidOtp) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        
        // Check if the email is the specific admin email
        if (!"usmaneletu2@gmail.com".equals(email)) {
            throw new RuntimeException("Access denied. You are not an admin.");
        }
        
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String jwt = jwtUtils.generateJwtTokenForUser(user);
        String refreshToken = jwtUtils.generateRefreshTokenForUser(user);
        
        UserProfile userProfile = new UserProfile();
        userProfile.setId(user.getId().toString());
        userProfile.setFirstName(user.getFirstName());
        userProfile.setLastName(user.getLastName());
        userProfile.setEmail(user.getEmail());
        userProfile.setRole(user.getRole().name());
        userProfile.setEnabled(user.isEnabled());

        return new JwtResponse(jwt, refreshToken, userProfile);
    }
    
    @Override
    public String initiateAdminOtpLogin(String email) {
        // Check if the email is the specific admin email
        if (!"usmaneletu2@gmail.com".equals(email)) {
            throw new RuntimeException("Access denied. You are not an admin.");
        }
        
        // Generate and send OTP
        return otpAuthService.generateAndSendOtp(email);
    }
    
    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Only update to INACTIVE if the user is currently ACTIVE
        if (user.getStatus() == User.Status.ACTIVE) {
            user.setStatus(User.Status.INACTIVE);
            userRepository.save(user);
            
            // Update officer profile status as well if it's an officer
            if (user.getRole() == User.Role.OFFICER) {
                OfficerProfile officerProfile = officerProfileRepository.findByUser(user)
                        .orElseThrow(() -> new RuntimeException("Officer profile not found for user: " + user.getId()));
                officerProfile.setStatus(User.Status.INACTIVE);
                officerProfileRepository.save(officerProfile);
            }
            
            // Update client profile status as well if it's a client
            if (user.getRole() == User.Role.CLIENT) {
                ClientProfile clientProfile = clientProfileRepository.findByUser(user)
                        .orElseThrow(() -> new RuntimeException("Client profile not found for user: " + user.getId()));
                clientProfile.setStatus(User.Status.INACTIVE);
                clientProfileRepository.save(clientProfile);
            }
        }
    }
}