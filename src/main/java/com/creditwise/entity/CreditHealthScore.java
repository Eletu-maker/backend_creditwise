package com.creditwise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "credit_health_scores", indexes = {
    @Index(name = "idx_score_client", columnList = "client_id"),
    @Index(name = "idx_score_date", columnList = "score_date")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreditHealthScore extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @NotNull(message = "Score date is required")
    @Column(name = "score_date", nullable = false)
    private java.time.LocalDate scoreDate;

    @Min(value = 0, message = "Score must be between 0 and 100")
    @Max(value = 100, message = "Score must be between 0 and 100")
    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "payment_history_score")
    private Integer paymentHistoryScore;

    @Column(name = "credit_utilization_score")
    private Integer creditUtilizationScore;

    @Column(name = "credit_age_score")
    private Integer creditAgeScore;

    @Column(name = "credit_mix_score")
    private Integer creditMixScore;

    @Column(name = "recent_activity_score")
    private Integer recentActivityScore;

    @Column(name = "disclaimer", length = 200)
    private String disclaimer;
}