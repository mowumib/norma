package com.hotelbooking.norma.modules.onboarding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;
import com.hotelbooking.norma.dto.request.LoginRequestDto;
import com.hotelbooking.norma.dto.request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.request.UpdatePasswordDto;
import com.hotelbooking.norma.modules.onboarding.service.OnboardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/onboarding/user/")
@RequiredArgsConstructor
@Validated
@Tag(name = "ONBOARDING REST APIS", description = "REST APIS FOR ONBOARDING USERS")
public class OnboardingController {

    private final OnboardingService service;
    @Operation(
        summary = "SIGNUP REST API",
        description = "REST API for users to register"
    )
    @PostMapping("/signup")
    public ResponseEntity<ResponseModel> RegisterUser(@Valid @RequestBody RegisterUserRequestDto dto) {
        ResponseModel responseModel = service.register(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "LOGIN REST API",
        description = "REST API for users to login"
    )
    @PostMapping("/login")
    public ResponseEntity<ResponseModel> loginUser(@Valid @RequestBody LoginRequestDto dto) {
        ResponseModel responseModel = service.login(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "Verify OTP Code REST API",
        description = "REST API to verify OTP Code"
    )
    @PostMapping("/verify-otp-code")
    public ResponseEntity<ResponseModel> verifyOtp(@RequestBody OtpTokenValidatorDto dto) {
        ResponseModel responseModel = service.verifyOtp(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "Resend OTP Code REST API",
        description = "REST API to resend OTP Code"
    )
    @PostMapping("/resend-otp-code")
    public ResponseEntity<ResponseModel> resendOtp(@RequestParam String email) {
        ResponseModel responseModel = service.resendOtp(email);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(
        summary = "Change Password REST API",
        description = "REST API to Update Password"
    )
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> changePassword(@RequestBody UpdatePasswordDto dto) {
        ResponseModel responseModel = service.changePassword(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(summary = "Forgot password") 
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseModel> forgotPassword(@RequestParam String email) {
       ResponseModel responseModel = service.forgotPassword(email);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @Operation(summary = "Reset password")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel> resetPassword(@RequestParam String email, @RequestParam String otpCode, @RequestParam String newPassword) {
       ResponseModel responseModel = service.resetPassword(email, otpCode, newPassword);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }
}
