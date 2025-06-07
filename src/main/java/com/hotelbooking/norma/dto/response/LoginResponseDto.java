package com.hotelbooking.norma.dto.response;

import lombok.Data;

@Data
public class LoginResponseDto {

    private Long id;
    private String email;
    private String token;

    public LoginResponseDto(Long id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }
}