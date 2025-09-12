package com.hotelbooking.norma.modules.onboarding.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hotelbooking.norma.entity.OTP;


@Repository
public interface OtpRepository extends JpaRepository<OTP, Long> {
    
    OTP findByUserCodeAndUsedFalse(String userCode);

    void deleteByUserCode(String userId);

    Optional<OTP> findByUserCodeAndOtpCode(String userCode, String otpCode);
    
    @Transactional
    void deleteByExpirationTimeBefore(LocalDateTime currentTime);
}
