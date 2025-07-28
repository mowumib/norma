package com.hotelbooking.norma.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.LoginRequestDto;
import com.hotelbooking.norma.dto.Request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.Request.UpdatePasswordDto;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;

public interface OnboardingService {
    public ResponseModel register(@RequestBody RegisterUserRequestDto dto);

    public ResponseModel login(@RequestBody LoginRequestDto dto);

    public ResponseModel verifyOtp(@RequestBody OtpTokenValidatorDto dto);

    public ResponseModel resendOtp(@RequestParam String email);

    public ResponseModel changePassword(@RequestBody UpdatePasswordDto dto);

    public ResponseModel forgotPassword(String email);

    public ResponseModel resetPassword(String email, String otpCode, String newPassword);
}
