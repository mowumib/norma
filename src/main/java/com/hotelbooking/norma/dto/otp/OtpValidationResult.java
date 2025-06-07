package com.hotelbooking.norma.dto.otp;

public class OtpValidationResult {

    private boolean success;
    private String message;

    // Constructors
    public OtpValidationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
