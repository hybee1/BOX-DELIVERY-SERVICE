package com.my_company.box_delivery_service.Exceptions;

import org.springframework.http.HttpStatus;


public class ItemException extends RuntimeException{

    public HttpStatus httpStatus;

    public ItemException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ItemException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}