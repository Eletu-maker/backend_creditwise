package com.creditwise.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.creditwise.dto.PaystackInitializeResponse;
import com.creditwise.service.PaystackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackServiceImpl implements PaystackService {

    @Value("${paystack.secret.key:}")
    private String paystackSecretKey;

    @Value("${paystack.api.url:https://api.paystack.co}")
    private String paystackApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public PaystackInitializeResponse initializePayment(String email, BigDecimal amount) {

        log.info("Initializing Paystack payment | email={} | amount={}", email, amount);

        try {

            String url = paystackApiUrl + "/transaction/initialize";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(paystackSecretKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Convert NGN → Kobo
            long paystackAmount = amount
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0)
                    .longValue();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("amount", paystackAmount);
            requestBody.put("currency", "NGN");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<PaystackInitializeResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PaystackInitializeResponse.class
            );

            PaystackInitializeResponse paystackResponse = response.getBody();

            if (paystackResponse != null && paystackResponse.isStatus()) {

                log.info(
                        "Paystack payment initialized successfully | reference={}",
                        paystackResponse.getData().getReference()
                );

                return paystackResponse;

            } else {

                String message = paystackResponse != null
                        ? paystackResponse.getMessage()
                        : "Unknown error";

                log.error("Paystack payment initialization failed | message={}", message);

                throw new RuntimeException("Payment initialization failed: " + message);
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            log.error("HTTP error calling Paystack API", e);

            throw new RuntimeException(
                    "Error initializing payment: " + e.getResponseBodyAsString()
            );

        } catch (Exception e) {

            log.error("Unexpected error initializing Paystack payment", e);

            throw new RuntimeException(
                    "Error initializing payment: " + e.getMessage()
            );
        }
    }

    @Override
    public String verifyPayment(String reference) {

        log.info("Verifying Paystack payment | reference={}", reference);

        try {

            String url = paystackApiUrl + "/transaction/verify/" + reference;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(paystackSecretKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            if (jsonNode.get("status").asBoolean()) {

                String status = jsonNode
                        .get("data")
                        .get("status")
                        .asText();

                log.info("Paystack verification result | status={}", status);

                return status;

            } else {

                String message = jsonNode
                        .get("message")
                        .asText();

                log.error("Paystack verification failed | message={}", message);

                throw new RuntimeException(
                        "Payment verification failed: " + message
                );
            }

        } catch (IOException e) {

            log.error("Error parsing Paystack response", e);

            throw new RuntimeException(
                    "Error verifying payment: " + e.getMessage()
            );

        } catch (Exception e) {

            log.error("Error calling Paystack verify API", e);

            throw new RuntimeException(
                    "Error verifying payment: " + e.getMessage()
            );
        }
    }
}