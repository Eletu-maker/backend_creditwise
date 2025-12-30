package com.creditwise.repository;

import com.creditwise.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
    
    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);
    
    @Modifying
    @Query("UPDATE Otp o SET o.used = true WHERE o.email = :email AND o.otpCode = :otpCode")
    void markAsUsed(@Param("email") String email, @Param("otpCode") String otpCode);
    
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredOtps();
    
    @Modifying
    @Query("DELETE FROM Otp o WHERE o.email = :email")
    void deleteByEmail(@Param("email") String email);
}