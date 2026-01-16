package com.hotelbooking.norma.modules.paystack.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface PaystackWebhookService {

    ResponseEntity<String> handleWebhook(HttpServletRequest request, String paystackSignature) throws IOException;
}
