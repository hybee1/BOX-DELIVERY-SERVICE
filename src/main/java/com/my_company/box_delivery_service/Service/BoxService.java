package com.my_company.box_delivery_service.Service;


import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTORequest.ItemDTORequest;
import com.my_company.box_delivery_service.DTOResponse.BoxDTOResponse;
import com.my_company.box_delivery_service.DTOResponse.ItemDTOResponse;
import com.my_company.box_delivery_service.Enum.BoxState;
import com.my_company.box_delivery_service.Enum.DeliveryStatus;
import com.my_company.box_delivery_service.Exceptions.BoxException;
import com.my_company.box_delivery_service.Exceptions.BoxNotFoundException;
import com.my_company.box_delivery_service.Exceptions.ItemException;
import com.my_company.box_delivery_service.Mapper.BoxMapper;
import com.my_company.box_delivery_service.Mapper.ItemMapper;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Delivery;
import com.my_company.box_delivery_service.Model.Item;
import com.my_company.box_delivery_service.Repo.BoxRepo;
import com.my_company.box_delivery_service.Repo.DeliveryRepo;
import com.my_company.box_delivery_service.Repo.ItemRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Service
public class BoxService {

    private final BoxRepo boxRepo;
    private final ItemRepo itemRepo;
    private final BoxMapper boxMapper;
    private final ItemMapper itemMapper;
    private final DeliveryRepo deliveryRepo;

    public BoxService(BoxRepo boxRepo, ItemRepo itemRepo,
                      BoxMapper boxMapper, ItemMapper itemMapper,
                      DeliveryRepo deliveryRepo) {
        this.boxRepo = boxRepo;
        this.itemRepo = itemRepo;
        this.boxMapper = boxMapper;
        this.itemMapper = itemMapper;
        this.deliveryRepo = deliveryRepo;
    }

    public ResponseEntity<?> createBox(BoxDTORequest boxRequest) {
        // convert BoxDTORequest to Box
        Box box = boxMapper.BoxDTORequestToBox(boxRequest);
        box.setState(BoxState.IDLE);
        Box box1 = boxRepo.save(box);

        // convert Box to BoxDTOResponse
        BoxDTOResponse boxDTOResponse = boxMapper.BoxToBoxDToResponse(box1);

        // convert BoxDTOResponse to json
        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("data", boxDTOResponse);

        return new ResponseEntity<>(finalResponse, HttpStatus.CREATED);

    }

    @Transactional
    public ResponseEntity<?> loadItems(String txRef, List<ItemDTORequest> items) {

        double boxTotalWeightLimit = 500.0;

        Box box = boxRepo.findById(txRef)
                            .orElseThrow(() -> new BoxNotFoundException("Box not found",
                                   HttpStatus.NOT_FOUND)
                            );

        if (box.getBatteryCapacity() < 25) {
            throw new BoxException("Battery too low to load items",
                    HttpStatus.BAD_REQUEST);
        }

        double newItemsTotalWeight = items.stream()
                                .mapToDouble(ItemDTORequest::getWeight)
                                .sum();

        double existingWeight = box.getItems().stream()
                                    .mapToDouble(Item::getWeight)
                                    .sum();

        double boxTotalWeight = newItemsTotalWeight + existingWeight;

        if (boxTotalWeight > boxTotalWeightLimit) {
            throw new BoxException("Weight exceeds box limit", HttpStatus.BAD_REQUEST);
        }

        box.setWeightLimit(boxTotalWeight);

        // update state and battery level with (newItemsTotalWeight,  1 min)
        Box b1 = updateBoxStateAndBatteryLevel2(box, newItemsTotalWeight,
                1, BoxState.LOADING);

        // convert itemRequest DTO to Item class
        List<Item> itemsList = items.stream()
                                .map(itemRequest ->
                                        itemMapper.ItemDTORequestToItem(b1, itemRequest)
                                )
                                .toList();

        List<Item> savedItemList = itemRepo.saveAll(itemsList);

        if(savedItemList.getFirst().getId()==null){
            throw new ItemException("Error saving items list", HttpStatus.EXPECTATION_FAILED);
        }

        // put list of items into box
        b1.getItems().addAll(savedItemList);

        // update state and battery level with (boxTotalWeight,  1 min)
        Box b2 = updateBoxStateAndBatteryLevel2(b1, boxTotalWeight,
                1, BoxState.LOADED);

        // update box weight limit
        b2.setWeightLimit(boxTotalWeight);

        Box b3 = boxRepo.save(b2);

        // convert Box to BoxDTOResponse
        BoxDTOResponse boxDTOResponse = boxMapper.BoxToBoxDToResponse(b3);

        // convert BoxDTOResponse to json
        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("data", boxDTOResponse);

        return new ResponseEntity<>(finalResponse, HttpStatus.OK);

    }

    public ResponseEntity<?> getItems(String txRef) {
        // List<Item> items = itemRepo.findByBox_TxRef(txRef);

        Box box = boxRepo.findByTxRefAndState(txRef, BoxState.LOADED)
                .orElseThrow(() ->
                        new BoxNotFoundException("Box not found in get items",
                                HttpStatus.NOT_FOUND)
                );

        // convert item to ItemDTOResponse
        List<ItemDTOResponse> itemDTOResponse = box.getItems().stream()
                            .map(itemMapper::ItemToItemDTOResponse
                            ).toList();

        // convert BoxDTOResponse to json
        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("data", itemDTOResponse);
        return new ResponseEntity<>(finalResponse, HttpStatus.OK);

    }

    public ResponseEntity<?> getAvailableBoxes() {
        List<Box> boxes = boxRepo.findByStateAndBatteryCapacityGreaterThanEqual(
                BoxState.IDLE, 25);

        // convert Box to BoxDTOResponse
        List<BoxDTOResponse> boxDTOResponse = boxes.stream()
                .map(boxMapper::BoxToBoxDToResponse
                ).toList();

        // convert BoxDTOResponse to json
        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("data", boxDTOResponse);


        return new ResponseEntity<>(finalResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getBatteryLevel(String txRef) {
        Box box = boxRepo.findById(txRef)
                .orElseThrow(() -> new BoxNotFoundException("Box not found",
                        HttpStatus.NOT_FOUND)
                );

        double batteryCapacity = box.getBatteryCapacity();

        HashMap<String, Object> tempResponse = new HashMap<>();
        tempResponse.put("boxTxRef", txRef);
        tempResponse.put("batteryCapacity", batteryCapacity);

        // convert BoxDTOResponse to json
        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("data", tempResponse);


        return new ResponseEntity<>(finalResponse, HttpStatus.OK);
    }

    public Box updateBoxStateAndBatteryLevel2(Box box,
                                              double weight, int timeInMinutes,
                                              BoxState newBoxState) {
        box.setState(newBoxState);
        double batteryCapacity = calculateBatteryLevelForNewState2(box, weight, timeInMinutes,
                newBoxState);
        double batteryCapacityTruncated  = Math.floor(batteryCapacity * 100.0) / 100.0;
        box.setBatteryCapacity(batteryCapacityTruncated);
        return boxRepo.save(box);
    }

    public Box updateBoxStateAndBatteryLevel2(Box box, int timeInMinutes,
                                              BoxState newBoxState) {
        box.setState(newBoxState);
        double batteryCapacity = calculateBatteryLevelForNewState2(box, timeInMinutes,
                newBoxState);
        double batteryCapacityTruncated  = Math.floor(batteryCapacity * 100.0) / 100.0;
        box.setBatteryCapacity(batteryCapacityTruncated);
        return boxRepo.save(box);
    }

    public double calculateBatteryLevelForNewState2(Box box,

            double weight, int timeInMinutes, BoxState newState) {

        double baseWeight = 500; // max weight in grams
        double baseDrain = 5;    // battery drain in percent
        int baseTimeDrain = 60;  // base time in minutes

        double weightRatio = weight / baseWeight;
        double timeRatio = (double) timeInMinutes / baseTimeDrain;

        switch (newState) {

            // LOADING consider weight and LOADING time before leaving the depot
            case LOADING: {
                double drain = baseDrain * weightRatio * timeRatio;
                return Math.max(box.getBatteryCapacity() - drain, 1);
            }

            // LOADED consider weight and waiting time before heading out for delivery
            // ARRIVED consider waiting time before offloading
            case LOADED : {
                double drain = baseDrain * weightRatio * timeRatio;
                return Math.max(box.getBatteryCapacity() - drain, 1);
            }

            // DELIVERING consider reducing weight and DELIVERING time at the
            // customer site so consider half drain of LOADING
            case DELIVERING: {
                double drain = baseDrain * weightRatio * timeRatio * 0.5;
                return Math.max(box.getBatteryCapacity() - drain, 1);
            }

            // EN ROUTE consider weight and journey time
            case ENROUTE: {
                double drain = baseDrain * weightRatio * timeRatio;
                return Math.max(box.getBatteryCapacity() - drain, 1);
            }

            default:
                return 0.0;
        }

    }

    public double calculateBatteryLevelForNewState2(Box box,

               int timeInMinutes, BoxState newState) {

        double baseDrain = 5;    // battery drain in percent
        int baseTimeDrain = 60;  // base time in minutes

        double timeRatio = (double) timeInMinutes / baseTimeDrain;

        switch (newState) {

            case IDLE, DELIVERED, RETURNING : {
                // Idle/Delivered/Returning trickle drains (per minute)
                final double CONSTANT_DRAIN_PER_MIN = 0.01;    // % per minute
                double drain = baseDrain * CONSTANT_DRAIN_PER_MIN * timeRatio;

                resetBox(box);

                return Math.max(box.getBatteryCapacity() - drain, 1);
            }

            default:
                return 0.0;
        }

    }

    public void updateDeliveryStatus2(String deliveryTrackingNo, Box box, DeliveryStatus status){
        Delivery delivery = deliveryRepo.findByDeliveryTrackingNo(deliveryTrackingNo)
                .orElseThrow(() -> {

                            return new BoxNotFoundException("Tracking number not found",
                                    HttpStatus.NOT_FOUND);
                }
                );


        // set delivery
        delivery.setStatus(status);

        if(box.getState().equals(BoxState.ENROUTE)) deliveryRepo.save(delivery);

        if(box.getState().equals(BoxState.DELIVERED)) deliveryRepo.save(delivery);

    }

    public void updateDeliveryStatus2(String deliveryTrackingNo){
        Delivery delivery = deliveryRepo.findByDeliveryTrackingNo(deliveryTrackingNo)
                .orElseThrow(() -> new BoxNotFoundException("Tracking number not found",
                        HttpStatus.NOT_FOUND)
                );

        // set delivery
        delivery.setStatus(DeliveryStatus.DELIVERY_UNSUCCESSFUL);

        deliveryRepo.save(delivery);

    }

    public void resetBox(Box box){
        box.setItems(new ArrayList<>());
        box.setWeightLimit(0);
    }


}
