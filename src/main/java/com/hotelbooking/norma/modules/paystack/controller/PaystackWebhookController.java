package com.hotelbooking.norma.modules.paystack.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.modules.paystack.service.PaystackWebhookService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/paystack")
public class PaystackWebhookController {

    private final PaystackWebhookService paystackWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request, @RequestHeader("x-paystack-signature") String paystackSignature) throws IOException {
         return paystackWebhookService.handleWebhook(request, paystackSignature);
    }

}
