package com.hotelbooking.norma.exception;

import org.springframework.http.HttpStatus;

public class GlobalRequestException extends RuntimeException{
	
	private final HttpStatus httpStatus;
	public GlobalRequestException(String message, HttpStatus httpStatus){
		super(message) ;
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
