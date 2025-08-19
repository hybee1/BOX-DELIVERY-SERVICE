package com.my_company.box_delivery_service.Mapper;

import com.my_company.box_delivery_service.DTOResponse.DeliveryStatusDTOResponse;
import com.my_company.box_delivery_service.Enum.DeliveryStatus;
import com.my_company.box_delivery_service.Model.Delivery;
import com.my_company.box_delivery_service.Model.DeliveryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusMapperTest {

    private DeliveryStatusMapper deliveryStatusMapper;

    @BeforeEach
    void setUp() {
        deliveryStatusMapper = new DeliveryStatusMapper();
    }

    @Test
    void testDeliveryToDeliveryStatusDTOResponse() {
        DeliveryItem item1 = DeliveryItem.builder()
                .name("Item-1")
                .code("ITEM_1")
                .weight(100.0)
                .build();

        DeliveryItem item2 = DeliveryItem.builder()
                .name("Item-2")
                .code("ITEM_2")
                .weight(150.0)
                .build();

        Delivery delivery = Delivery.builder()
                .deliveryTrackingNo("TRACK123")
                .txRef("BOX_001")
                .totalItemWeight(250.0)
                .items(List.of(item1, item2))
                .status(DeliveryStatus.STILL_AT_DEPOT)
                .build();

        DeliveryStatusDTOResponse dto = deliveryStatusMapper.deliveryToDeliveryStatusDTOResponse(delivery);

        assertNotNull(dto);
        assertEquals(delivery.getDeliveryTrackingNo(), dto.getTrackingNo());
        assertEquals(delivery.getTxRef(), dto.getTxRef());
        assertEquals(delivery.getTotalItemWeight(), dto.getTotalItemWeight());
        assertEquals(delivery.getItems(), dto.getItems());
        assertEquals(delivery.getStatus().name(), dto.getStatus());
    }
}
