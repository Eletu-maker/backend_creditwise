package com.creditwise.dto;

import com.creditwise.entity.ClientProfile;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClientProfileDto {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Integer creditScore;
    private ClientProfile.PlanStatus planStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static ClientProfileDto fromEntity(ClientProfile profile) {
        ClientProfileDto dto = new ClientProfileDto();
        dto.setId(profile.getId());
        // Access the user ID through the user field
        dto.setUserId(profile.getUser().getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setAddress(profile.getAddress());
        dto.setCreditScore(profile.getCreditScore());
        dto.setPlanStatus(profile.getPlanStatus());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        dto.setCreatedBy(profile.getCreatedBy());
        dto.setUpdatedBy(profile.getUpdatedBy());
        return dto;
    }
}