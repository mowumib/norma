package com.hotelbooking.norma.paystack.controller;

import com.google.common.hash.Hashing;
import com.hotelbooking.norma.paystack.service.PaystackWebhookService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/paystack")
@RequiredArgsConstructor
public class PaystackWebhookController {
    private final PaystackWebhookService paystackWebhookService;

    @Value("${paystack.secret-key}")
    private String secretKey; 

    // Webhook entry point with signature verification
    @PostMapping("/webhook")
    public ResponseEntity<String> handlePaystackWebhook(@RequestHeader("x-paystack-signature") String signature,
                                                        @RequestBody String payload) {
        // Verify the signature before processing the request
        String computedSignature = Hashing.sha512().hashString(payload + secretKey.trim(), StandardCharsets.UTF_8)
                .toString();
        if (!signature.equals(computedSignature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        boolean processed = paystackWebhookService.processWebhookEvent(payload);
        if (processed) {
            return ResponseEntity.ok("Webhook processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process webhook");
        }
    }
}