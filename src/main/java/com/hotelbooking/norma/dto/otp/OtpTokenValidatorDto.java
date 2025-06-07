package com.hotelbooking.norma.dto.otp;

import lombok.Data;

@Data
public class OtpTokenValidatorDto {
    private String email;
    private String otpCode;
}
