package com.hotelbooking.norma.service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.LoginRequestDto;
import com.hotelbooking.norma.dto.Request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.Request.UpdatePasswordDto;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;

public interface OnboardingService {
    public ResponseModel register(RegisterUserRequestDto dto);

    public ResponseModel login(LoginRequestDto dto);

    public ResponseModel verifyOtp(OtpTokenValidatorDto dto);

    public ResponseModel resendOtp(String email);

    public ResponseModel changePassword(UpdatePasswordDto dto);

    public ResponseModel forgotPassword(String email);

    public ResponseModel resetPassword(String email, String otpCode, String newPassword);
}
