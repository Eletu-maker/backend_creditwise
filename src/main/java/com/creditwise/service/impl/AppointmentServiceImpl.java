package com.creditwise.service.impl;

import com.creditwise.entity.Appointment;
import com.creditwise.entity.User;
import com.creditwise.repository.AppointmentRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.AppointmentService;
import com.creditwise.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Appointment createAppointment(UUID clientId, UUID officerId, LocalDateTime appointmentDatetime, Appointment.AppointmentType appointmentType, String reason) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        // Check if an appointment already exists at the same time
        if (appointmentRepository.findByClientIdAndOfficerIdAndAppointmentDatetime(clientId, officerId, appointmentDatetime).isPresent()) {
            throw new RuntimeException("An appointment already exists at this time");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .officer(officer)
                .appointmentDatetime(appointmentDatetime)
                .appointmentType(appointmentType)
                .reason(reason)
                .appointmentStatus(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment updateAppointment(UUID appointmentId, Appointment.AppointmentStatus status, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        appointment.setAppointmentStatus(status);
        if (notes != null) {
            appointment.setNotes(notes);
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        appointment.setAppointmentStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsForClient(UUID clientId) {
        return appointmentRepository.findByClientId(clientId);
    }

    @Override
    public List<Appointment> getAppointmentsForOfficer(UUID officerId) {
        return appointmentRepository.findByOfficerId(officerId);
    }

    @Override
    public List<Appointment> getUpcomingAppointmentsForUser(UUID userId) {
        return appointmentRepository.findByUserIdAndAppointmentDatetimeBetween(
                userId, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
    }

    @Override
    public Appointment getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
    }

    @Override
    public List<Appointment> getAppointmentsBetween(UUID userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return appointmentRepository.findByUserIdAndAppointmentDatetimeBetween(userId, startDateTime, endDateTime);
    }

    @Override
    public void notifyOfficerOfAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        
        // Send notification to officer (in a real app, this might send an email, push notification, etc.)
        // For now, just mark as notified
        appointment.setNotified(true);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getUnnotifiedAppointments() {
        return appointmentRepository.findByNotifiedFalse();
    }
}