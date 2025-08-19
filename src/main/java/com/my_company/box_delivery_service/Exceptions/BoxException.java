package com.my_company.box_delivery_service.Exceptions;

import org.springframework.http.HttpStatus;


public class BoxException extends RuntimeException{

    public HttpStatus httpStatus;

    public BoxException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BoxException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}