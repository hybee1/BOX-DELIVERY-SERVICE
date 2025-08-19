package com.my_company.box_delivery_service.Enum;


import com.my_company.box_delivery_service.Exceptions.DeliveryException;
import org.springframework.http.HttpStatus;

public enum DeliveryStatus {
    STILL_AT_DEPOT,
    ARRIVED,
    LOADING,
    LOADED,
    DELIVERING,
    DELIVERY_SUCCESSFUL,
    ENROUTE,
    DELIVERY_UNSUCCESSFUL,

    DeliveryStatus();


    public static DeliveryStatus fromName(String name) {
        if(name.equalsIgnoreCase("DELIVERED")){
            return DELIVERY_SUCCESSFUL;
        }

        for (DeliveryStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new DeliveryException("No Delivery status can be mapped to this name: " +
                name, HttpStatus.NOT_FOUND);
    }
}

