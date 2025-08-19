package com.my_company.box_delivery_service.Exceptions;

import org.springframework.http.HttpStatus;


public class ItemNotFoundException extends RuntimeException{

    public HttpStatus httpStatus;

    public ItemNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ItemNotFoundException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}