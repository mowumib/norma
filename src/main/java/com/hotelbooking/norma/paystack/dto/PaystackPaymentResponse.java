package com.hotelbooking.norma.paystack.dto;

import lombok.Data;

@Data
public class PaystackPaymentResponse {
    private String status;
    private String message;
    private PaystackData data;

    @Data
    public static class PaystackData {
        private String authorization_url;  // URL to redirect the user for payment
        private String access_code;        // Access code for the transaction
        private String reference;          // Unique transaction reference
    }    
}
