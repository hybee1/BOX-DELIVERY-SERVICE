package com.my_company.box_delivery_service.Exceptions;

import org.springframework.http.HttpStatus;


public class DeliveryException extends RuntimeException{

    public HttpStatus httpStatus;

    public DeliveryException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public DeliveryException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}