package com.creditwise.service;

import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.entity.User;
import com.creditwise.security.JwtUtils;
import com.creditwise.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateUser_ShouldReturnJwtResponse_WhenValidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        User user = User.builder()
                .id(java.util.UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role(User.Role.CLIENT)
                .isEnabled(true)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken(authentication)).thenReturn("refreshToken");

        // Act
        JwtResponse response = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUserProfile());
        assertEquals("test@example.com", response.getUserProfile().getEmail());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        verify(jwtUtils).generateRefreshToken(authentication);
    }

    @Test
    void registerClient_ShouldReturnJwtResponse_WhenValidRequest() {
        // Arrange
        RegisterClientRequest registerRequest = new RegisterClientRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("1234567890");

        User user = User.builder()
                .id(java.util.UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(User.Role.CLIENT)
                .isEnabled(true)
                .build();

        Authentication authentication = mock(Authentication.class);

        when(userService.createUser(registerRequest)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken(authentication)).thenReturn("refreshToken");

        // Act
        JwtResponse response = authService.registerClient(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUserProfile());
        assertEquals("john.doe@example.com", response.getUserProfile().getEmail());
        
        verify(userService).createUser(registerRequest);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        verify(jwtUtils).generateRefreshToken(authentication);
    }
}