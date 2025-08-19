package com.my_company.box_delivery_service.Exceptions;

import org.springframework.http.HttpStatus;


public class BoxNotFoundException extends RuntimeException{

    public HttpStatus httpStatus;

    public BoxNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BoxNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}