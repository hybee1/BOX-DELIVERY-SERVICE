package com.my_company.box_delivery_service.Mapper;

import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTOResponse.BoxDTOResponse;
import com.my_company.box_delivery_service.Enum.BoxState;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Delivery;
import com.my_company.box_delivery_service.Model.Item;
import com.my_company.box_delivery_service.Repo.BoxRepo;
import com.my_company.box_delivery_service.Repo.DeliveryItemRepo;
import com.my_company.box_delivery_service.Repo.DeliveryRepo;
import com.my_company.box_delivery_service.Service.BoxService;
import com.my_company.box_delivery_service.Service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoxMapperTest {

    @Mock
    private DeliveryRepo deliveryRepo;

    @Mock
    private BoxRepo boxRepo;

    @Mock
    private BoxMapper boxMapper;

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private BoxService boxService;

    @BeforeEach
    void setup() {
        // optional: init mocks manually if not using MockitoExtension
        // MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        boxMapper = new BoxMapper();
    }

    @Test
    void testStartDelivery_Success_ReturnsResponse() {
        String txRef = "BOX_002";

        // Arrange: create a loaded box with one item
        Box box = new Box();
        box.setTxRef(txRef);
        box.setBatteryCapacity(50);
        box.setState(BoxState.LOADED);

        // Use builder to create Item
        Item item1 = Item.builder()
                .name("Item-1")
                .weight(100.0)
                .code("ITEM_1")
                .box(box)
                .build();

        box.setItems(List.of(item1));

        // Mock Delivery repository
        Delivery savedDelivery = Delivery.builder()
                .deliveryTrackingNo("TRACK_123")
                .txRef(txRef)
                .build();

        when(boxRepo.findById(txRef)).thenReturn(Optional.of(box));
        when(deliveryRepo.save(any(Delivery.class))).thenReturn(savedDelivery);

        // Act
        ResponseEntity<?> response = deliveryService.startDelivery(txRef);

        // Assert response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("deliveryTrackingNo"));
        assertEquals("TRACK_123", body.get("deliveryTrackingNo"));
        assertEquals("delivery started", body.get("status"));
        assertEquals(txRef, body.get("txRef"));

        // Verify repository save
        verify(deliveryRepo, times(1)).save(any(Delivery.class));

    }

    @Test
    void testBoxDTORequestToBox() {
        BoxDTORequest request = new BoxDTORequest();
        // BoxDTORequest request = BoxDTORequest.builder()
                request.setTxRef("BOX_002");


        Box box = boxMapper.BoxDTORequestToBox(request);

        assertNotNull(box);
        assertEquals(request.getTxRef(), box.getTxRef());
        // other fields are not mapped in current implementation, so they remain default
    }
}
