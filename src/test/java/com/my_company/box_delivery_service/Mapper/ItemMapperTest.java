package com.my_company.box_delivery_service.Mapper;

import com.my_company.box_delivery_service.DTORequest.ItemDTORequest;
import com.my_company.box_delivery_service.DTOResponse.ItemDTOResponse;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
    }

    @Test
    void testItemToItemDTOResponse() {
        Box box = Box.builder()
                .txRef("BOX_001")
                .build();

        Item item = Item.builder()
                .name("Item-1")
                .code("ITEM_1")
                .weight(100.5)
                .box(box)
                .build();

        ItemDTOResponse dto = itemMapper.ItemToItemDTOResponse(item);

        assertNotNull(dto);
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getCode(), dto.getCode());
        assertEquals(item.getWeight(), dto.getWeight());
        assertEquals(item.getBox().getTxRef(), dto.getBoxTxRef());
    }

    @Test
    void testItemDTORequestToItem() {
        Box box = Box.builder()
                .txRef("BOX_002")
                .build();

        ItemDTORequest dtoRequest = new ItemDTORequest();
        dtoRequest.setName("Item-2");
        dtoRequest.setCode("ITEM_2");
        dtoRequest.setWeight(150.0);

        Item item = itemMapper.ItemDTORequestToItem(box, dtoRequest);

        assertNotNull(item);
        assertEquals(dtoRequest.getName(), item.getName());
        assertEquals(dtoRequest.getCode(), item.getCode());
        assertEquals(dtoRequest.getWeight(), item.getWeight());
        assertEquals(box, item.getBox());
    }
}
