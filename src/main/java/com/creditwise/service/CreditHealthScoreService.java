package com.creditwise.service;

import com.creditwise.dto.CreditHealthScoreDto;
import com.creditwise.entity.CreditHealthScore;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CreditHealthScoreService {
    CreditHealthScore createCreditHealthScore(CreditHealthScoreDto scoreDto);
    List<CreditHealthScore> getScoresByClient(UUID clientId);
    CreditHealthScore getScoreByDate(UUID clientId, LocalDate date);
    List<CreditHealthScore> getScoresByDateRange(UUID clientId, LocalDate startDate, LocalDate endDate);
    CreditHealthScore calculateCreditScore(UUID clientId);
    
    // Method to get the latest score for a client
    CreditHealthScore getLatestScoreForClient(UUID clientId);
}