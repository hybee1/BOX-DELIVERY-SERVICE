package com.my_company.box_delivery_service.Mapper;

import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTOResponse.DeliveryStatusDTOResponse;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Delivery;
import org.springframework.stereotype.Component;


@Component
public class DeliveryStatusMapper {

    public DeliveryStatusDTOResponse deliveryToDeliveryStatusDTOResponse(
                                             Delivery delivery) {

       return DeliveryStatusDTOResponse.builder()
               .trackingNo(delivery.getDeliveryTrackingNo())
               .txRef(delivery.getTxRef())
               .totalItemWeight(delivery.getTotalItemWeight())
               .items(delivery.getItems())
               .status(delivery.getStatus().name())
               .build();
    }



}


