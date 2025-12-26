package com.creditwise.repository;

import com.creditwise.entity.Appointment;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByClientId(UUID clientId);
    List<Appointment> findByOfficerId(UUID officerId);
    List<Appointment> findByClientIdAndAppointmentDatetimeAfter(UUID clientId, LocalDateTime dateTime);
    List<Appointment> findByOfficerIdAndAppointmentDatetimeAfter(UUID officerId, LocalDateTime dateTime);
    List<Appointment> findByAppointmentDatetimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT a FROM Appointment a WHERE (a.client.id = :userId OR a.officer.id = :userId) AND a.appointmentDatetime BETWEEN :startDateTime AND :endDateTime")
    List<Appointment> findByUserIdAndAppointmentDatetimeBetween(UUID userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    Optional<Appointment> findByClientIdAndOfficerIdAndAppointmentDatetime(UUID clientId, UUID officerId, LocalDateTime appointmentDatetime);
    
    // Add method to find appointments that need notification
    List<Appointment> findByNotifiedFalse();
}