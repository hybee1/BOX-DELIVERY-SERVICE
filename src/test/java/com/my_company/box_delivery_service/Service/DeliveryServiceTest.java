package com.my_company.box_delivery_service.Service;


import com.my_company.box_delivery_service.DTOResponse.DeliveryStatusDTOResponse;
import com.my_company.box_delivery_service.Enum.BoxState;
import com.my_company.box_delivery_service.Enum.DeliveryStatus;
import com.my_company.box_delivery_service.Exceptions.BoxException;
import com.my_company.box_delivery_service.Exceptions.BoxNotFoundException;
import com.my_company.box_delivery_service.Exceptions.DeliveryException;
import com.my_company.box_delivery_service.Mapper.DeliveryStatusMapper;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Delivery;
import com.my_company.box_delivery_service.Model.Item;
import com.my_company.box_delivery_service.Repo.BoxRepo;
import com.my_company.box_delivery_service.Repo.DeliveryItemRepo;
import com.my_company.box_delivery_service.Repo.DeliveryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private BoxRepo boxRepo;

    @Mock
    private BoxService boxService;

    @Mock
    private DeliveryRepo deliveryRepo;

    @Mock
    private DeliveryItemRepo deliveryItemRepo;

    @Mock
    private DeliveryStatusMapper deliveryStatusMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test Start delivery
    @Test
    void testStartDelivery_BoxNotFound_ThrowsException() {
        String txRef = "BOX_002";

        when(boxRepo.findById(txRef)).thenReturn(Optional.empty());

        BoxNotFoundException exception = assertThrows(BoxNotFoundException.class,
                () -> deliveryService.startDelivery(txRef));

        assertEquals("Box not found", exception.getMessage());
        verify(boxRepo, times(1)).findById(txRef);
        verifyNoInteractions(boxService, deliveryRepo, deliveryItemRepo);
    }

    @Test
    void testStartDelivery_BatteryTooLow_ThrowsException() {
        String txRef = "BOX_004";
        Box box = new Box();
        box.setTxRef(txRef);
        box.setBatteryCapacity(10);
        box.setState(BoxState.LOADED);

        when(boxRepo.findById(txRef)).thenReturn(Optional.of(box));

        BoxException exception = assertThrows(BoxException.class,
                () -> deliveryService.startDelivery(txRef));

        assertEquals("Battery too low to start delivery", exception.getMessage());
    }

    @Test
    void testStartDelivery_BoxNotLoaded_ThrowsException() {
        String txRef = "BOX_003";
        Box box = new Box();
        box.setTxRef(txRef);
        box.setBatteryCapacity(50);
        box.setState(BoxState.IDLE);

        when(boxRepo.findById(txRef)).thenReturn(Optional.of(box));

        BoxException exception = assertThrows(BoxException.class,
                () -> deliveryService.startDelivery(txRef));

        assertEquals("Only a loaded box can be sent out for delivery", exception.getMessage());
    }

    @Test
    void testStartDelivery_Success_ReturnsResponse() {
        String txRef = "BOX_002";

        // Arrange
        Box box = new Box();
        box.setTxRef(txRef);
        box.setBatteryCapacity(50);
        box.setState(BoxState.LOADED);
        Item item1 = Item.builder().name("Item-1").code("ITEM_1").weight(100).box(box).build();
        box.setItems(List.of(item1));

        // Mock the repositories
        Delivery savedDelivery = Delivery.builder()
                .deliveryTrackingNo("tracking_no_123")
                .txRef(txRef)
                .build();

        when(boxRepo.findById(txRef)).thenReturn(Optional.of(box));
        when(deliveryRepo.save(any(Delivery.class))).thenReturn(savedDelivery);

        // Act
        ResponseEntity<?> response = deliveryService.startDelivery(txRef);

        // Assert: response is correct
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("deliveryTrackingNo"));
        assertEquals("tracking_no_123", body.get("deliveryTrackingNo"));
        assertEquals("delivery started", body.get("status"));
        assertEquals(txRef, body.get("txRef"));

        // Verify repository save
        verify(deliveryRepo, times(1)).save(any(Delivery.class));

    }

    @Test
    void testCheckDeliveryStatus_BoxNotFound_ThrowsException() {
        String trackingNo = "tracking_no_123";

        when(deliveryRepo.findByDeliveryTrackingNo(trackingNo)).thenReturn(Optional.empty());

        BoxNotFoundException exception = assertThrows(BoxNotFoundException.class,
                () -> deliveryService.checkDeliveryStatus(trackingNo));

        assertEquals("Tracking number not found", exception.getMessage());
    }

    @Test
    void testCheckDeliveryStatus_Success_ReturnsResponse() {
        String trackingNo = "tracking_no_123";

        Delivery delivery = Delivery.builder()
                .deliveryTrackingNo(trackingNo)
                .status(DeliveryStatus.STILL_AT_DEPOT)
                .build();

        DeliveryStatusDTOResponse dtoResponse = new DeliveryStatusDTOResponse();
        dtoResponse.setStatus("STILL_AT_DEPOT");

        when(deliveryRepo.findByDeliveryTrackingNo(trackingNo)).thenReturn(Optional.of(delivery));
        when(deliveryStatusMapper.deliveryToDeliveryStatusDTOResponse(delivery)).thenReturn(dtoResponse);

        ResponseEntity<?> response = deliveryService.checkDeliveryStatus(trackingNo);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> body = (java.util.Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("data"));
        assertEquals(dtoResponse, body.get("data"));
    }
}
