package com.creditwise.service;

import com.creditwise.entity.Appointment;
import com.creditwise.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    Appointment createAppointment(UUID clientId, UUID officerId, LocalDateTime appointmentDatetime, Appointment.AppointmentType appointmentType, String reason);
    Appointment updateAppointment(UUID appointmentId, Appointment.AppointmentStatus status, String notes);
    void cancelAppointment(UUID appointmentId);
    List<Appointment> getAppointmentsForClient(UUID clientId);
    List<Appointment> getAppointmentsForOfficer(UUID officerId);
    List<Appointment> getUpcomingAppointmentsForUser(UUID userId);
    Appointment getAppointmentById(UUID appointmentId);
    List<Appointment> getAppointmentsBetween(UUID userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    // New method for notification
    void notifyOfficerOfAppointment(UUID appointmentId);
    List<Appointment> getUnnotifiedAppointments();
}