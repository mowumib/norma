package com.hotelbooking.norma.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.LoginRequestDto;
import com.hotelbooking.norma.dto.Request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.Request.UpdatePasswordDto;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;
import com.hotelbooking.norma.service.OnboardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
@Validated
@Tag(name = "ONBOARDING REST APIS", description = "REST APIS FOR ONBOARDING USERS")
public class OnboardingController {

    private final OnboardingService service;
    @Operation(
        summary = "SIGNUP REST API",
        description = "REST API for users to register"
    )
    @PostMapping("/user/signup")
    public ResponseEntity<ResponseModel> RegisterUser(@Valid @RequestBody RegisterUserRequestDto dto) {
        ResponseModel responseModel = service.registerUser(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "LOGIN REST API",
        description = "REST API for users to login"
    )
    @PostMapping("/user/login")
    public ResponseEntity<ResponseModel> loginUser(@Valid @RequestBody LoginRequestDto dto) {
        ResponseModel responseModel = service.loginUser(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "Verify OTP Code REST API",
        description = "REST API to verify OTP Code"
    )
    @PostMapping("/user/verify-otp-code")
    public ResponseEntity<ResponseModel> verifyOtpCode(@RequestBody OtpTokenValidatorDto dto) {
        ResponseModel responseModel = service.verifyOtpCode(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "Resend OTP Code REST API",
        description = "REST API to resend OTP Code"
    )
    @PostMapping("/user/resend-otp-code")
    public ResponseEntity<ResponseModel> resendOtpCode(@RequestParam String email) {
        ResponseModel responseModel = service.resendOtpCode(email);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "Resend OTP Code REST API",
        description = "REST API to resend OTP Code"
    )
    @PostMapping("/user/update-password")
    public ResponseEntity<ResponseModel> updatePassword(@RequestBody UpdatePasswordDto dto) {
        ResponseModel responseModel = service.updatePassword(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }
}
