package com.hotelbooking.norma.paystack.service;

public interface PaystackWebhookService {
    boolean processWebhookEvent(String payload);
}
