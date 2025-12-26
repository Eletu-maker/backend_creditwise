package com.creditwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UserProfile userProfile;

    public JwtResponse(String accessToken, String refreshToken, UserProfile userProfile) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userProfile = userProfile;
    }
}