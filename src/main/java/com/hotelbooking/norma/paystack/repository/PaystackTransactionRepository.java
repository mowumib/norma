package com.hotelbooking.norma.paystack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.norma.paystack.entity.PaystackTransaction;

import java.util.Optional;

public interface PaystackTransactionRepository extends JpaRepository<PaystackTransaction, Long> {
    Optional<PaystackTransaction> findByReference(String reference);
}
