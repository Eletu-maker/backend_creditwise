package com.creditwise.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_client", columnList = "client_id"),
        @Index(name = "idx_appointment_officer", columnList = "officer_id"),
        @Index(name = "idx_appointment_datetime", columnList = "appointment_datetime")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDatetime;

    @Column(name = "appointment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "appointment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;

    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "notified", nullable = false)
    private boolean notified = false;

    public enum AppointmentType {
        CONSULTATION,
        REVIEW,
        FOLLOW_UP,
        EMERGENCY
    }

    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        NO_SHOW
    }
    
    public boolean isNotified() {
        return notified;
    }
    
    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}