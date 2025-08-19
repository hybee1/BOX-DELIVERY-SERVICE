package com.my_company.box_delivery_service.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value={BoxException.class})
    public ResponseEntity<Object> handleBoxException(
            BoxException boxException) {

        Map<String, String> errorMessageObj = Map.of(
                "errorMessage", boxException.getMessage());
        Map<String, Object> error = Map.of(
                "error", errorMessageObj);

        ErrorResponse errorResponse = new ErrorResponse(
                error, boxException.getCause()  );

        return new ResponseEntity<>(errorResponse, boxException.httpStatus );

    }

    @ExceptionHandler(value={BoxNotFoundException.class})
    public ResponseEntity<Object> handleBoxNotFoundException(
            BoxNotFoundException boxNotFoundException) {

        Map<String, String> errorMessageObj = Map.of(
                "errorMessage", boxNotFoundException.getMessage());
        Map<String, Object> error = Map.of(
                "error", errorMessageObj);

        ErrorResponse errorResponse = new ErrorResponse(
                error, boxNotFoundException.getCause()  );

        return new ResponseEntity<>(errorResponse, boxNotFoundException.httpStatus );

    }

    @ExceptionHandler(value={ItemException.class})
    public ResponseEntity<Object> handleItemException(
            ItemException itemException) {

        Map<String, String> errorMessageObj = Map.of(
                "errorMessage", itemException.getMessage());
        Map<String, Object> error = Map.of(
                "error", errorMessageObj);

        ErrorResponse errorResponse = new ErrorResponse(
                error, itemException.getCause()  );

        return new ResponseEntity<>(errorResponse, itemException.httpStatus );

    }

    @ExceptionHandler(value={ItemNotFoundException.class})
    public ResponseEntity<Object> handleItemNotFoundException(
            ItemNotFoundException itemNotFoundException) {

        Map<String, String> errorMessageObj = Map.of(
                "errorMessage", itemNotFoundException.getMessage());
        Map<String, Object> error = Map.of(
                "error", errorMessageObj);

        ErrorResponse errorResponse = new ErrorResponse(
                error, itemNotFoundException.getCause()  );

        return new ResponseEntity<>(errorResponse, itemNotFoundException.httpStatus );

    } //

    @ExceptionHandler(value={DeliveryException.class})
    public ResponseEntity<Object> handleDeliveryException(
            DeliveryException deliveryException) {

        Map<String, String> errorMessageObj = Map.of(
                "errorMessage", deliveryException.getMessage());
        Map<String, Object> error = Map.of(
                "error", errorMessageObj);

        ErrorResponse errorResponse = new ErrorResponse(
                error, deliveryException.getCause()  );

        return new ResponseEntity<>(errorResponse, deliveryException.httpStatus );

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage()); // this shows your @Min/@Max messages
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


}


