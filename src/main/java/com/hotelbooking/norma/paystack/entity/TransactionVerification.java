package com.hotelbooking.norma.paystack.entity;

import java.time.LocalDateTime;

import com.hotelbooking.norma.utils.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionVerification extends BaseEntity{
    private String transactionReference;
    private long transactionId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean isVerified;
    private String status;
    private String gatewayResponse;
    private int amount;
    private String paidAt;
    private String channel;
    private String currency;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}