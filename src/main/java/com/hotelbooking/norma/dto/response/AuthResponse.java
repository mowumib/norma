package com.hotelbooking.norma.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String userCode;
    private String email;
    private String token;

}