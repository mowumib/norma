package com.hotelbooking.norma.paystack.dto;

import lombok.Data;

@Data
public class PaystackPaymentRequest {
    private String email; 
    private int amount;
    private String currency = "NGN";
}
