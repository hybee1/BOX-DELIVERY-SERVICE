package com.my_company.box_delivery_service.Exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor

public class ErrorResponse {

    //private final String message;
    private final Map<String, Object> message;
    private final Throwable throwable;

}
