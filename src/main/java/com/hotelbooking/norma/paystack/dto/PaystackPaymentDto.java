package com.hotelbooking.norma.paystack.dto;

import lombok.Data;

@Data
public class PaystackPaymentDto {
    private String bookingCode;
    private String userCode;
    private int amount;
    private String email;

}
