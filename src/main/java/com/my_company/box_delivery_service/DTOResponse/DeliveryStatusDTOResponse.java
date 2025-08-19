package com.my_company.box_delivery_service.DTOResponse;


import com.my_company.box_delivery_service.Enum.DeliveryStatus;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.DeliveryItem;
import com.my_company.box_delivery_service.Model.Item;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class DeliveryStatusDTOResponse {

    private String trackingNo;

    private String txRef;

    private double totalItemWeight;  // weightLimit

   // private double batteryCapacity;

   // private String boxState;

    private List<DeliveryItem> items;

    private String status;

}
