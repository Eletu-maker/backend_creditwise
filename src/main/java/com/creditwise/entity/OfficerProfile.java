package com.creditwise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "officer_profiles")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OfficerProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Max active clients is required")
    @Min(value = 1, message = "Max active clients must be at least 1")
    @Max(value = 100, message = "Max active clients cannot exceed 100")
    @Column(name = "max_active_clients", nullable = false)
    private Integer maxActiveClients;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "specialization", length = 100)
    private String specialization;
}