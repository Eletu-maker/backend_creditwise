package com.creditwise.service;

import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse registerClient(RegisterClientRequest registerRequest);
}