package com.hotelbooking.norma.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        GlobalException globalException = new GlobalException(
            errorMessage,
            HttpStatus.BAD_REQUEST,
            ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(globalException, HttpStatus.BAD_REQUEST);
    }

}
