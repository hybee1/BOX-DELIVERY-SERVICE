package com.my_company.box_delivery_service.DTOResponse;


import com.my_company.box_delivery_service.Model.Item;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoxDTOResponse {

    private String txRef;

    private double weightLimit;

   private double batteryCapacity;

   private String boxState;

    private List<Item> items;

}
