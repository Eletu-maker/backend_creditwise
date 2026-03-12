package com.creditwise.service;

public interface WebhookService {

    void storeWebhookEvent(String payload, String signature);

}