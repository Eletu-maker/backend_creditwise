package com.creditwise.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.creditwise.dto.PaystackInitializeResponse;
import com.creditwise.dto.PaymentDto;
import java.util.Map;
public interface PaymentService {

    PaystackInitializeResponse.DataResponse initializePayment(String email, BigDecimal amount);

    void verifyAndRecordPayment(String reference);
    
    List<PaymentDto> getPaymentHistory(UUID userId);
    
    PaymentDto getPaymentByReference(String reference);

    void processWebhook(Map<String, Object> payload, String signature);
}