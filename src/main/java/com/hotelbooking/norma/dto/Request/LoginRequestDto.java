package com.hotelbooking.norma.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotEmpty(message = "Email address can not be a null or empty")
    private String email;

    private String password;
}
