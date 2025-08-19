package com.my_company.box_delivery_service.Mapper;

import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTORequest.ItemDTORequest;
import com.my_company.box_delivery_service.DTOResponse.ItemDTOResponse;
import com.my_company.box_delivery_service.DTOResponse.ItemDTOResponse;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Item;
import org.springframework.stereotype.Component;


@Component
public class ItemMapper {

    public ItemDTOResponse ItemToItemDTOResponse(Item item) {
        
       return ItemDTOResponse.builder()
               .name(item.getName())
               .weight(item.getWeight())
               .code(item.getCode())
               .boxTxRef(item.getBox().getTxRef())
               .build();
    }

    public Item ItemDTORequestToItem(Box box, ItemDTORequest itemDTORequest){

        return Item.builder()
                .name(itemDTORequest.getName())
                .weight(itemDTORequest.getWeight())
                .code(itemDTORequest.getCode())
                .box(box)
                .build();
    }

}


