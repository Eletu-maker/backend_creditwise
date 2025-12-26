package com.creditwise.service;

import com.creditwise.dto.CreditHealthScoreDto;
import com.creditwise.entity.CreditHealthScore;
import com.creditwise.entity.User;
import com.creditwise.repository.CreditHealthScoreRepository;
import com.creditwise.repository.UserRepository;
import com.creditwise.service.impl.CreditHealthScoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreditHealthScoreServiceTest {

    @Mock
    private CreditHealthScoreRepository creditHealthScoreRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreditHealthScoreServiceImpl creditHealthScoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCreditHealthScore_ShouldCreateScore_WhenValidRequest() {
        // Arrange
        CreditHealthScoreDto scoreDto = new CreditHealthScoreDto();
        UUID clientId = UUID.randomUUID();
        scoreDto.setClientId(clientId.toString());
        scoreDto.setScoreDate(LocalDate.now());
        scoreDto.setScore(85);
        scoreDto.setPaymentHistoryScore(90);
        scoreDto.setCreditUtilizationScore(80);
        scoreDto.setCreditAgeScore(75);
        scoreDto.setCreditMixScore(70);
        scoreDto.setRecentActivityScore(65);
        scoreDto.setDisclaimer("This is not an official credit score");

        User client = User.builder().id(clientId).role(User.Role.CLIENT).build();
        CreditHealthScore creditHealthScore = CreditHealthScore.builder()
                .id(UUID.randomUUID())
                .client(client)
                .scoreDate(LocalDate.now())
                .score(85)
                .paymentHistoryScore(90)
                .creditUtilizationScore(80)
                .creditAgeScore(75)
                .creditMixScore(70)
                .recentActivityScore(65)
                .disclaimer("This is not an official credit score")
                .build();

        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(creditHealthScoreRepository.save(any(CreditHealthScore.class))).thenReturn(creditHealthScore);

        // Act
        CreditHealthScore result = creditHealthScoreService.createCreditHealthScore(scoreDto);

        // Assert
        assertNotNull(result);
        assertEquals(85, result.getScore());
        assertEquals(client, result.getClient());
        assertEquals("This is not an official credit score", result.getDisclaimer());
        
        verify(userRepository).findById(clientId);
        verify(creditHealthScoreRepository).save(any(CreditHealthScore.class));
    }

    @Test
    void calculateCreditScore_ShouldReturnCalculatedScore() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        User client = User.builder().id(clientId).role(User.Role.CLIENT).build();
        
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(creditHealthScoreRepository.save(any(CreditHealthScore.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CreditHealthScore result = creditHealthScoreService.calculateCreditScore(clientId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getScore() >= 0 && result.getScore() <= 100);
        assertNotNull(result.getDisclaimer());
        assertEquals("This is not an official credit score", result.getDisclaimer());
        
        verify(userRepository).findById(clientId);
        verify(creditHealthScoreRepository).save(any(CreditHealthScore.class));
    }
}