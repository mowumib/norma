package com.hotelbooking.norma.paystack.entity;

import java.time.LocalDateTime;

import com.hotelbooking.norma.paystack.enums.PaystackPaymentStatus;
import com.hotelbooking.norma.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity{

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "booking_code")
    private String bookingCode;

    @Column(name = "email")
    private String email;

    @Column(name = "reference")
    private String reference;

    @Column(name = "amount")
    private double amount;

    @Column(name = "paid_at", nullable = true)
    private LocalDateTime paidAt;

    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt = LocalDateTime.now();

    @Column(name = "channel")
    private String channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "paystack_payment_status")
    private PaystackPaymentStatus paystackPaymentStatus;
}
