package com.hotelbooking.norma.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hotelbooking.norma.entity.otp.OTP;


@Repository
public interface OtpRepository extends JpaRepository<OTP, Long> {
    
    OTP findByUserIdAndUsedFalse(String userId);

    void deleteByUserId(String userId);

    @Transactional
    void deleteByExpirationTimeBefore(LocalDateTime currentTime);
}
