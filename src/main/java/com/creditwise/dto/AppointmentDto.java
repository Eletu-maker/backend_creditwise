package com.creditwise.dto;

import com.creditwise.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private UUID id;
    private UUID clientId;
    private String clientName;
    private UUID officerId;
    private String officerName;
    private LocalDateTime appointmentDatetime;
    private Appointment.AppointmentType appointmentType;
    private String reason;
    private Appointment.AppointmentStatus appointmentStatus;
    private String notes;
    private boolean notified;

    public static AppointmentDto fromEntity(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setClientId(appointment.getClient().getId());
        dto.setClientName(appointment.getClient().getFirstName() + " " + appointment.getClient().getLastName());
        dto.setOfficerId(appointment.getOfficer().getId());
        dto.setOfficerName(appointment.getOfficer().getFirstName() + " " + appointment.getOfficer().getLastName());
        dto.setAppointmentDatetime(appointment.getAppointmentDatetime());
        dto.setAppointmentType(appointment.getAppointmentType());
        dto.setReason(appointment.getReason());
        dto.setAppointmentStatus(appointment.getAppointmentStatus());
        dto.setNotes(appointment.getNotes());
        dto.setNotified(appointment.isNotified());
        
        return dto;
    }
}