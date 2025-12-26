package com.creditwise.dto;

import lombok.Data;

@Data
public class UserProfile {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean isEnabled;
}