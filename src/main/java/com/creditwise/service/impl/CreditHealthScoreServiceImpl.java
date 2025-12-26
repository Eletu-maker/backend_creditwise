package com.creditwise.service.impl;

import com.creditwise.dto.CreditHealthScoreDto;
import com.creditwise.entity.CreditHealthScore;
import com.creditwise.entity.User;
import com.creditwise.exception.ResourceNotFoundException;
import com.creditwise.repository.CreditHealthScoreRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.CreditHealthScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.Optional;

@Service
public class CreditHealthScoreServiceImpl implements CreditHealthScoreService {

    @Autowired
    private CreditHealthScoreRepository creditHealthScoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CreditHealthScore createCreditHealthScore(CreditHealthScoreDto scoreDto) {
        // Convert the clientId string to UUID before passing to repository
        UUID clientIdUUID;
        try {
            clientIdUUID = UUID.fromString(scoreDto.getClientId());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Client", "id", scoreDto.getClientId());
        }
        
        User client = userRepository.findById(clientIdUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientIdUUID));

        CreditHealthScore score = CreditHealthScore.builder()
                .client(client)
                .score(scoreDto.getScore())
                .paymentHistoryScore(scoreDto.getPaymentHistoryScore())
                .creditUtilizationScore(scoreDto.getCreditUtilizationScore())
                .creditAgeScore(scoreDto.getCreditAgeScore())
                .creditMixScore(scoreDto.getCreditMixScore())
                .recentActivityScore(scoreDto.getRecentActivityScore())
                .disclaimer(scoreDto.getDisclaimer())
                .scoreDate(scoreDto.getScoreDate() != null ? scoreDto.getScoreDate() : LocalDate.now())
                .build();

        return creditHealthScoreRepository.save(score);
    }

    @Override
    public List<CreditHealthScore> getScoresByClient(UUID clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return creditHealthScoreRepository.findByClientOrderByScoreDateDesc(client);
    }

    @Override
    public CreditHealthScore getScoreByDate(UUID clientId, LocalDate date) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return creditHealthScoreRepository.findByClientAndScoreDate(client, date)
                .orElseThrow(() -> new ResourceNotFoundException("CreditHealthScore", "client and date", clientId + " and " + date));
    }

    @Override
    public List<CreditHealthScore> getScoresByDateRange(UUID clientId, LocalDate startDate, LocalDate endDate) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return creditHealthScoreRepository.findByClientAndScoreDateBetweenOrderByScoreDateAsc(client, startDate, endDate);
    }

    @Override
    public CreditHealthScore calculateCreditScore(UUID clientId) {
        // Implementation for calculating credit score
        // This would contain the actual credit score calculation logic
        // For now, returning a dummy score
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        CreditHealthScore score = CreditHealthScore.builder()
                .client(client)
                .score(750) // Dummy value
                .paymentHistoryScore(85)
                .creditUtilizationScore(90)
                .creditAgeScore(80)
                .creditMixScore(75)
                .recentActivityScore(70)
                .disclaimer("This is a calculated score based on available data")
                .scoreDate(LocalDate.now())
                .build();

        return creditHealthScoreRepository.save(score);
    }

    @Override
    public CreditHealthScore getLatestScoreForClient(UUID clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        List<CreditHealthScore> scores = creditHealthScoreRepository.findByClientOrderByScoreDateDesc(client);
        
        if (scores.isEmpty()) {
            return null;
        }
        
        // Return the most recent score (first in the list since it's ordered by date descending)
        return scores.get(0);
    }
}