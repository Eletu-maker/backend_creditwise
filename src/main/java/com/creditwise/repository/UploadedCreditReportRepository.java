package com.creditwise.repository;

import com.creditwise.entity.UploadedCreditReport;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UploadedCreditReportRepository extends JpaRepository<UploadedCreditReport, UUID> {
    List<UploadedCreditReport> findByClientOrderByUploadDateDesc(User client);
    List<UploadedCreditReport> findByClientAndIsExpiredFalseOrderByUploadDateDesc(User client);
}