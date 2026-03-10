package com.creditwise.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;
import com.creditwise.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    
    private UUID id;
    private String reference;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String currency;
    private String userEmail;
    private UUID userId;
    
    public static PaymentDto fromEntity(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .reference(payment.getReference())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .currency(payment.getCurrency())
                .userEmail(payment.getUser().getEmail())
                .userId(payment.getUser().getId())
                .build();
    }
}
