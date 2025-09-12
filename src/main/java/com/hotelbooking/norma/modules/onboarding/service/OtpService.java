package com.hotelbooking.norma.modules.onboarding.service;

import com.hotelbooking.norma.dto.otp.OtpValidationResult;

public interface OtpService {

    public String generateOtp();

    public void saveOtp(String userCode, String otpCode);

    public OtpValidationResult validateOtp(String userCode, String otpCode);
    
    public void deleteOtpByUserCode(String userCode);

    public String requestOtp(String userCode);


}
