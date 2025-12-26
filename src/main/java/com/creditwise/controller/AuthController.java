package com.creditwise.controller;

import com.creditwise.dto.ApiResponse;
import com.creditwise.dto.JwtResponse;
import com.creditwise.dto.LoginRequest;
import com.creditwise.dto.RegisterClientRequest;
import com.creditwise.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register-client")
    public ResponseEntity<ApiResponse<JwtResponse>> registerClient(@Valid @RequestBody RegisterClientRequest registerRequest) {
        JwtResponse response = authService.registerClient(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Client registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }
}