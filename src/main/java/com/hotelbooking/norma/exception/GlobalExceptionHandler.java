package com.hotelbooking.norma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(value = {GlobalRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(GlobalRequestException e) {
        // HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        HttpStatus status = e.getHttpStatus();
        log.error("An error occurred: {}", e.getMessage(), e);
        GlobalException globalException = new GlobalException(e.getMessage(), status, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(globalException, status);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {
        HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("An error occurred: {}", e.getMessage(), e);
        GlobalException globalException = new GlobalException(e.getMessage(), serverError, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(globalException, serverError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());

        GlobalException globalException = new GlobalException(
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED,
            ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(globalException, HttpStatus.UNAUTHORIZED);
    }
}
