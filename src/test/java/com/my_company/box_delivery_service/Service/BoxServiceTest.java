package com.my_company.box_delivery_service.Service;


import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTORequest.ItemDTORequest;
import com.my_company.box_delivery_service.DTOResponse.BoxDTOResponse;
import com.my_company.box_delivery_service.DTOResponse.ItemDTOResponse;
import com.my_company.box_delivery_service.Enum.BoxState;
import com.my_company.box_delivery_service.Enum.DeliveryStatus;
import com.my_company.box_delivery_service.Exceptions.BoxException;
import com.my_company.box_delivery_service.Exceptions.BoxNotFoundException;
import com.my_company.box_delivery_service.Mapper.BoxMapper;
import com.my_company.box_delivery_service.Mapper.ItemMapper;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Delivery;
import com.my_company.box_delivery_service.Model.Item;
import com.my_company.box_delivery_service.Repo.BoxRepo;
import com.my_company.box_delivery_service.Repo.DeliveryRepo;
import com.my_company.box_delivery_service.Repo.ItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BoxServiceTest {

    @InjectMocks
    private BoxService boxService;

    @Mock
    private BoxRepo boxRepo;

    @Mock
    private ItemRepo itemRepo;

    @Mock
    private DeliveryRepo deliveryRepo;

    @Mock
    private BoxMapper boxMapper;

    @Mock
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // Test createBox
    @Test
    void testCreateBox_ShouldReturnCreatedResponse() {
        BoxDTORequest request = new BoxDTORequest();
        Box boxEntity = new Box();
        Box savedBox = new Box();
        BoxDTOResponse boxDTOResponse = new BoxDTOResponse();

        when(boxMapper.BoxDTORequestToBox(request)).thenReturn(boxEntity);
        when(boxRepo.save(boxEntity)).thenReturn(savedBox);
        when(boxMapper.BoxToBoxDToResponse(savedBox)).thenReturn(boxDTOResponse);

        ResponseEntity<?> response = boxService.createBox(request);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("data"));

        verify(boxRepo, times(1)).save(boxEntity);
    }

    // loadItems exceeding weight limit
    @Test
    void testLoadItems_ShouldThrowBoxException_WhenExceedWeightLimit() {
        // create box with battery level and empty items list
        Box box = Box.builder()
                .txRef("BOX_001")
                .batteryCapacity(50)
                .weightLimit(400) // initial weight this will be updated later
                .state(BoxState.IDLE)
                .build();

        // ensure items list is not null
        if (box.getItems() == null) {
            box.setItems(new ArrayList<>());
        }

        // create existing item that belongs to this box
        Item existingItem = Item.builder()
                .name("ExistingItem")
                .weight(400.0)
                .code("CODE_EXISTING")
                .box(box)
                .build();

        // add existing item to box
        box.getItems().add(existingItem);

        // mock repository to return this box
        when(boxRepo.findById("BOX_001")).thenReturn(Optional.of(box));

        // new item that would exceed box weight limit
        List<ItemDTORequest> items = List.of(
                new ItemDTORequest("ItemX", 200.0, "CODE_X")
        );

        // assert that BoxException is thrown
        BoxException exception = assertThrows(BoxException.class, () ->
                boxService.loadItems("BOX_001", items)
        );

        assertEquals("Weight exceeds box limit", exception.getMessage());
    }

    // Test loadItems - battery too low
    @Test
    void testLoadItems_ShouldThrowBoxException_WhenBatteryTooLow() {
        Box box = new Box();
        box.setTxRef("BOX_001");
        box.setBatteryCapacity(20); // low battery

        when(boxRepo.findById("BOX_001")).thenReturn(Optional.of(box));

        List<ItemDTORequest> items = List.of(new ItemDTORequest());

        BoxException exception = assertThrows(BoxException.class, () ->
                boxService.loadItems("BOX_001", items)
        );

        assertEquals("Battery too low to load items", exception.getMessage());
    }

    // Test loadItems - box not found
    @Test
    void testLoadItems_ShouldThrowBoxNotFoundException_WhenBoxMissing() {
        when(boxRepo.findById("BOX_002")).thenReturn(Optional.empty());

        List<ItemDTORequest> items = List.of(new ItemDTORequest());

        assertThrows(BoxNotFoundException.class, () ->
                boxService.loadItems("BOX_002", items)
        );
    }

    // Test getItems
    @Test
    void testGetItems_BoxExists_ReturnsItemDTOList() {
        // Arrange
        String txRef = "BOX_002";

        Box box = new Box();
        box.setTxRef(txRef);
        box.setState(BoxState.LOADED);
        box.setItems(new ArrayList<>()); // ensure items list is not null

        // create items
        Item item1 = new Item();
        item1.setName("Item-1");
        item1.setCode("ITEM_1");
        item1.setWeight(100);
        item1.setBox(box);

        Item item2 = new Item();
        item2.setName("Item-2");
        item2.setCode("ITEM_2");
        item2.setWeight(200);
        item2.setBox(box);

        box.getItems().add(item1);
        box.getItems().add(item2);

        // Mock repository and mapper
        when(boxRepo.findByTxRefAndState(txRef, BoxState.LOADED))
                .thenReturn(Optional.of(box));

        when(itemMapper.ItemToItemDTOResponse(item1))
                .thenReturn(new ItemDTOResponse("Item-1", 100.0, "ITEM_1", txRef));
        when(itemMapper.ItemToItemDTOResponse(item2))
                .thenReturn(new ItemDTOResponse("Item-2", 200.0, "ITEM_2", txRef));

        // Act
        ResponseEntity<?> response = boxService.getItems(txRef);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        @SuppressWarnings("unchecked")
        List<ItemDTOResponse> data = (List<ItemDTOResponse>) ((Map<String, Object>)response.getBody()).get("data");

        assertEquals(2, data.size());
        assertEquals("Item-1", data.get(0).getName());
        assertEquals("Item-2", data.get(1).getName());
        assertEquals(txRef, data.get(0).getBoxTxRef());
        assertEquals(txRef, data.get(1).getBoxTxRef());

        // Verify interactions
        verify(boxRepo, times(1)).findByTxRefAndState(txRef, BoxState.LOADED);
        verify(itemMapper, times(1)).ItemToItemDTOResponse(item1);
        verify(itemMapper, times(1)).ItemToItemDTOResponse(item2);
    }

    // Test getItems
    @Test
    void testGetItems_BoxNotFound_ThrowsException() {
        String txRef = "NON_EXISTENT_BOX";

        when(boxRepo.findByTxRefAndState(txRef, BoxState.LOADED)).thenReturn(Optional.empty());

        BoxNotFoundException exception = assertThrows(BoxNotFoundException.class,
                () -> boxService.getItems(txRef));

        assertEquals("Box not found in get items", exception.getMessage());

        verify(boxRepo, times(1)).findByTxRefAndState(txRef, BoxState.LOADED);
        verifyNoInteractions(itemMapper);
    }

    // Test getAvailableBoxes
    @Test
    void testGetAvailableBoxes_ShouldReturnOnlyIdleBoxesWithBatteryAbove25() {
        Box box1 = new Box();
        box1.setTxRef("BOX_001");
        box1.setState(BoxState.IDLE);
        box1.setBatteryCapacity(50);

        Box box2 = new Box();
        box2.setTxRef("BOX_002");
        box2.setState(BoxState.IDLE);
        box2.setBatteryCapacity(20); // should be filtered out

        when(boxRepo.findByStateAndBatteryCapacityGreaterThanEqual(BoxState.IDLE, 25))
                .thenReturn(List.of(box1));

        when(boxMapper.BoxToBoxDToResponse(box1)).thenReturn(new BoxDTOResponse());

        var response = boxService.getAvailableBoxes();
        var body = (java.util.Map<?, ?>) response.getBody();

        assertNotNull(body);
        List<?> dataList = (List<?>) body.get("data");
        assertEquals(1, dataList.size());
    }

    // Test getBatteryLevel
    @Test
    void testGetBatteryLevel_ShouldReturnBattery() {
        Box box = new Box();
        box.setTxRef("BOX_001");
        box.setBatteryCapacity(80.5);

        when(boxRepo.findById("BOX_001")).thenReturn(Optional.of(box));

        ResponseEntity<?> response = boxService.getBatteryLevel("BOX_001");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        var data = (java.util.Map<?, ?>) ((java.util.Map<?, ?>) response.getBody()).get("data");
        assertEquals(80.5, data.get("batteryCapacity"));

        verify(boxRepo, times(1)).findById("BOX_001");
    }

    // updateBoxStateAndBatteryLevel2 calculations
    @Test
    void testUpdateBoxStateAndBatteryLevel2_ShouldReduceBattery() {
        Box box = new Box();
        box.setBatteryCapacity(100);
        box.setWeightLimit(200);
        box.setState(BoxState.IDLE);

        when(boxRepo.save(any(Box.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Box updatedBox = boxService.updateBoxStateAndBatteryLevel2(box, 200, 60, BoxState.LOADING);

        assertEquals(BoxState.LOADING, updatedBox.getState());
        assertTrue(updatedBox.getBatteryCapacity() < 100); // battery reduced
        verify(boxRepo, times(1)).save(box);
    }

    // Testing method calculateBatteryLevelForNewState2(Box, double, int, BoxState)
    @Test
    void TestCalculateBatteryLevelForNewState2_WithWeightTimeAndState() {
        Box box = new Box();
        box.setBatteryCapacity(100);

        // LOADING state
        double resultLoading = boxService.calculateBatteryLevelForNewState2(box, 250, 30, BoxState.LOADING);
        // drain = 5 * (250/500) * (30/60) = 1.25
        assertEquals(98.75, resultLoading, 0.01);

        // LOADED state
        double resultLoaded = boxService.calculateBatteryLevelForNewState2(box, 500, 60, BoxState.LOADED);
        // drain = 5 * 1 * 1 = 5
        assertEquals(95.0, resultLoaded, 0.01);

        // DELIVERING state
        double resultDelivering = boxService.calculateBatteryLevelForNewState2(box, 400, 30, BoxState.DELIVERING);
        // drain = 5 * (400/500) * (30/60) * 0.5 = 1.0
        assertEquals(99.0, resultDelivering, 0.01);

        // ENROUTE state
        double resultEnroute = boxService.calculateBatteryLevelForNewState2(box, 100, 15, BoxState.ENROUTE);
        // drain = 5 * (100/500) * (15/60) = 0.25
        assertEquals(99.75, resultEnroute, 0.01);

        // Default / unknown state
        double resultDefault = boxService.calculateBatteryLevelForNewState2(box, 100, 15, BoxState.IDLE);
        assertEquals(0.0, resultDefault);
    }

    // Testing method calculateBatteryLevelForNewState2(Box, int, BoxState)
    @Test
    void testCalculateBatteryLevelForNewState2_WithTimeAndState() {
        Box box = new Box();
        box.setBatteryCapacity(50);
        box.setWeightLimit(100);

        // IDLE state
        double resultIdle = boxService.calculateBatteryLevelForNewState2(box, 60, BoxState.IDLE);
        // drain = 5 * 0.01 * (60/60) = 0.05
        assertEquals(49.95, resultIdle, 0.01);
        assertEquals(0, box.getWeightLimit());
        assertNotNull(box.getItems());
        assertTrue(box.getItems().isEmpty());

        // DELIVERED state
        box.setBatteryCapacity(80);
        double resultDelivered = boxService.calculateBatteryLevelForNewState2(box, 30, BoxState.DELIVERED);
        // drain = 5 * 0.01 * (30/60) = 0.025
        assertEquals(79.975, resultDelivered, 0.01);

        // RETURNING state
        box.setBatteryCapacity(60);
        double resultReturning = boxService.calculateBatteryLevelForNewState2(box, 60, BoxState.RETURNING);
        // drain = 5 * 0.01 * 1 = 0.05
        assertEquals(59.95, resultReturning, 0.01);

        // Default / unknown state
        double resultDefault = boxService.calculateBatteryLevelForNewState2(box, 15, BoxState.LOADING);
        assertEquals(0.0, resultDefault);
    }

    // updateDeliveryStatus2 behavior
    @Test
    void testUpdateDeliveryStatus2_ShouldUpdateDeliveryStatus() {
        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.STILL_AT_DEPOT);

        when(deliveryRepo.findByDeliveryTrackingNo("tracking_no_001"))
                .thenReturn(java.util.Optional.of(delivery));
        when(deliveryRepo.save(delivery)).thenReturn(delivery);

        Box box = new Box();
        box.setState(BoxState.ENROUTE);

        boxService.updateDeliveryStatus2("tracking_no_001", box, DeliveryStatus.ENROUTE);

        assertEquals(DeliveryStatus.ENROUTE, delivery.getStatus());
        verify(deliveryRepo, times(1)).save(delivery);
    }


    // resetBox clears items and weightLimit
    @Test
    void testResetBox_ShouldClearItemsAndResetWeightLimit() {
        Box box = new Box();
        box.setItems(new ArrayList<>(List.of(new Item(), new Item())));
        box.setWeightLimit(200);

        boxService.resetBox(box);

        assertNotNull(box.getItems());
        assertEquals(0, box.getItems().size());
        assertEquals(0, box.getWeightLimit());
    }
}