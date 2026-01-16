package com.hotelbooking.norma.modules.paystack.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.norma.modules.paystack.entity.TransactionVerification;



public interface TransactionVerificationRepository extends JpaRepository<TransactionVerification, Long> {
    Optional<TransactionVerification> findByTransactionReference(String reference);
}
