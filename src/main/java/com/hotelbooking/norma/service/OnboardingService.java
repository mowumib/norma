package com.hotelbooking.norma.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.LoginRequestDto;
import com.hotelbooking.norma.dto.Request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.Request.UpdatePasswordDto;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;

public interface OnboardingService {
    public ResponseModel registerUser(@RequestBody RegisterUserRequestDto dto);

    public ResponseModel loginUser(@RequestBody LoginRequestDto dto);

    public ResponseModel verifyOtpCode(@RequestBody OtpTokenValidatorDto dto);

    public ResponseModel resendOtpCode(@RequestParam String email);

    public ResponseModel updatePassword(@RequestBody UpdatePasswordDto dto);
}
