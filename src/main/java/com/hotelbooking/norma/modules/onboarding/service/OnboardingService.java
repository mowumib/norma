package com.hotelbooking.norma.modules.onboarding.service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;
import com.hotelbooking.norma.dto.request.LoginRequestDto;
import com.hotelbooking.norma.dto.request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.request.UpdatePasswordDto;

public interface OnboardingService {
    public ResponseModel register(RegisterUserRequestDto dto);

    public ResponseModel login(LoginRequestDto dto);

    public ResponseModel verifyOtp(OtpTokenValidatorDto dto);

    public ResponseModel resendOtp(String email);

    public ResponseModel changePassword(UpdatePasswordDto dto);

    public ResponseModel forgotPassword(String email);

    public ResponseModel resetPassword(String email, String otpCode, String newPassword);
}
