package com.creditwise.repository;

import com.creditwise.entity.OfficerProfile;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficerProfileRepository extends JpaRepository<OfficerProfile, UUID> {
    Optional<OfficerProfile> findByUser(User user);
}