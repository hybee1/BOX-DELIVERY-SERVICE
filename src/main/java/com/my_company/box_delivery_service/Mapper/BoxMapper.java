package com.my_company.box_delivery_service.Mapper;

import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTOResponse.BoxDTOResponse;
import com.my_company.box_delivery_service.Model.Box;
import org.springframework.stereotype.Component;


@Component
public class BoxMapper {

    public BoxDTOResponse BoxToBoxDToResponse(Box box) {

       return BoxDTOResponse.builder()
               .txRef(box.getTxRef())
               .weightLimit(box.getWeightLimit())
               .batteryCapacity(box.getBatteryCapacity())
               .boxState(box.getState().name())
               .items(box.getItems())
               .build();
    }

    public Box BoxDTORequestToBox(BoxDTORequest boxDTORequest){
        return Box.builder()
                .txRef(boxDTORequest.getTxRef())
//                .weightLimit(boxDTORequest.getWeightLimit())
//                // .batteryCapacity(boxDTORequest.getBatteryCapacity())
//                // .boxState(boxDTORequest.getState().name())
//                .items(boxDTORequest.getItems())
                .build();
    }

}


