package com.creditwise.repository;

import com.creditwise.entity.CreditHealthScore;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditHealthScoreRepository extends JpaRepository<CreditHealthScore, UUID> {
    List<CreditHealthScore> findByClientOrderByScoreDateDesc(User client);
    Optional<CreditHealthScore> findByClientAndScoreDate(User client, LocalDate scoreDate);
    List<CreditHealthScore> findByClientAndScoreDateBetween(User client, LocalDate startDate, LocalDate endDate);
    
    // Add the method for date range with ordering
    List<CreditHealthScore> findByClientAndScoreDateBetweenOrderByScoreDateAsc(User client, LocalDate startDate, LocalDate endDate);
}