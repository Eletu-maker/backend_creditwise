package com.creditwise.service;
import com.creditwise.dto.PaystackInitializeResponse;
import java.math.BigDecimal;

public interface  PaystackService {
     PaystackInitializeResponse initializePayment(String email, BigDecimal amount);

    String verifyPayment(String reference);
}
