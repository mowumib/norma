package com.hotelbooking.norma.serviceImpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.otp.OtpValidationResult;
import com.hotelbooking.norma.entity.OTP;
import com.hotelbooking.norma.repository.OtpRepository;
import com.hotelbooking.norma.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpRepository otpRepository;

    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public void saveOtp(String userCode, String otpCode) {
        // Set expiration time to 5 minutes from now
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        // Check if OTP exists for user, if yes update, else create new
        OTP existingOTP = otpRepository.findByUserCodeAndUsedFalse(userCode);
        if (existingOTP != null) {
            existingOTP.setOtpCode(otpCode);
            existingOTP.setExpirationTime(expirationTime);
            existingOTP.setUsed(false);
            otpRepository.save(existingOTP);
        } else {
            OTP otp = new OTP();
            otp.setUserCode(userCode);
            otp.setOtpCode(otpCode);
            otp.setExpirationTime(expirationTime);
            otp.setUsed(false);
            otpRepository.save(otp);
        }
    }

    @Override
    public OtpValidationResult validateOtp(String userCode, String otpCode) {
        LocalDateTime now = LocalDateTime.now();

        // Optional: clean up expired OTPs
        otpRepository.deleteByExpirationTimeBefore(now);

        OTP otp = otpRepository.findByUserCodeAndUsedFalse(userCode);
        if (otp == null) {
            return new OtpValidationResult(false, "No active OTP found or it has already been used.");
        }

        if (!otp.getOtpCode().equals(otpCode)) {
            return new OtpValidationResult(false, "Invalid OTP code.");
        }

        if (otp.getExpirationTime().isBefore(now)) {
            return new OtpValidationResult(false, "OTP has expired. Please request a new one.");
        }

        otp.setUsed(true);
        otpRepository.save(otp);
        return new OtpValidationResult(true, "OTP verified successfully.");
    }

    @Override
    public void deleteOtpByUserCode(String userCode) {
        otpRepository.deleteByUserCode(userCode);
    }

    @Override
    public String requestOtp(String userCode) {
        String newOTP = generateOtp();
        saveOtp(userCode, newOTP);
        return String.valueOf(newOTP);
        
    }
}
