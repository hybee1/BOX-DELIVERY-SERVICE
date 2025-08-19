package com.my_company.box_delivery_service.DTORequest;


import com.my_company.box_delivery_service.Model.Item;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;


@Data
@AllArgsConstructor
public class BoxDTORequest {

    @NotBlank(message = "this field can not be empty and the max characters " +
            "allowed is 20")
    @Size(max = 20, message = "the max characters allowed is 20")
    private String txRef;

//    @NotBlank(message = "this field can not be empty and the max weight can " +
//            "not exceed 500 grams")
//    @Min(value = 0, message = "the box min weight must be 0 grams")
//    @Max(value = 500, message = "the box max weight can not exceed 500 grams")
//    private double weightLimit;

//    @NotBlank(message = "this field can not be empty and the max percentage can " +
//            "not exceed 100. input number without % sign")
//    @Min(value=0, message = "the box min battery level is 0%")
//    @Max(value=100, message = "the box max battery level can not exceed 100%")
//    private int batteryCapacity;

//    @NotBlank(message = "this field can not be empty, the field can be one of the " +
//            "following (ARRIVED, IDLE, LOADING, LOADED, DELIVERING, DELIVERED, " +
//            "ENROUTE, RETURNING)")
//    private String boxState;

//    @NotBlank
//    private List<Item> items;

}
