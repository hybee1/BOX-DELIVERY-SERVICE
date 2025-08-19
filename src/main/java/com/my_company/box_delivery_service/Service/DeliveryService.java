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
import com.my_company.box_delivery_service.Model.DeliveryItem;
import com.my_company.box_delivery_service.Repo.BoxRepo;
import com.my_company.box_delivery_service.Repo.DeliveryItemRepo;
import com.my_company.box_delivery_service.Repo.DeliveryRepo;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class DeliveryService {

    private final BoxRepo boxRepo;
    private final BoxService boxService;
    private final DeliveryRepo deliveryRepo;
    private final DeliveryItemRepo deliveryItemRepo;
    private final DeliveryStatusMapper deliveryStatusMapper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DeliveryService(BoxRepo boxRepo, BoxService boxService,
                           @Lazy DeliveryRepo deliveryRepo,
                           DeliveryItemRepo deliveryItemRepo,
                           DeliveryStatusMapper deliveryStatusMapper) {
        this.boxRepo = boxRepo;
        this.boxService = boxService;
        this.deliveryRepo = deliveryRepo;
        this.deliveryItemRepo = deliveryItemRepo;
        this.deliveryStatusMapper = deliveryStatusMapper;
    }

    @Transactional
    public ResponseEntity<?> startDelivery(String txRef) {
        Box box = boxRepo.findById(txRef)
                .orElseThrow(() -> new BoxNotFoundException("Box not found", HttpStatus.NOT_FOUND));

        if (box.getBatteryCapacity() < 25) {
            throw new BoxException("Battery too low to start delivery", HttpStatus.BAD_REQUEST);
        }

        if (box.getState() != BoxState.LOADED) {
            throw new BoxException("Only a loaded box can be sent out for delivery", HttpStatus.BAD_REQUEST);
        }


        // create tracking number and other fields in delivery
        Delivery delivery = Delivery.builder()
                            .deliveryTrackingNo(UUID.randomUUID().toString())
                            .txRef(box.getTxRef())
                          //  .items(deliveryItemList)
                            .items(new ArrayList<>())
                            .totalItemWeight(box.getWeightLimit())
                            .status(DeliveryStatus.STILL_AT_DEPOT)
                            .build();

        // create delivery
        // first copy/create delivery items list associated to this delivery from box table
        // or item table itself

        // create delivery items directly from box items
        box.getItems().forEach(item -> {
            DeliveryItem deliveryItem = DeliveryItem.builder()
                    .name(item.getName())
                    .weight(item.getWeight())
                    .code(item.getCode())
                    .build();
            delivery.addItem(deliveryItem);

        });


        List<DeliveryItem> deliveryItemList = box.getItems().stream()
                .map(item -> DeliveryItem.builder()
                        .name(item.getName())
                        .weight(item.getWeight())
                        .code(item.getCode())
                        .delivery(delivery)
                        .build()
                )
                .toList();

        Delivery delivery1 = deliveryRepo.save(delivery);
        if(delivery1.getDeliveryTrackingNo().isBlank()){
            throw new DeliveryException("Unable to issue tracking number for this delivery",
                    HttpStatus.EXPECTATION_FAILED);
        }

        executor.submit(() -> simulateDelivery(box, delivery1));

        Map<String, Object> response = new HashMap<>();
        response.put("status", "delivery started");
        response.put("txRef", txRef);

        response.put("deliveryTrackingNo", delivery1.getDeliveryTrackingNo());

//        response.put("deliveryTrackingNo",
//                deliveryItemList1.getFirst().getDelivery().getDeliveryTrackingNo());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void simulateDelivery(Box box, Delivery delivery) {
        try {
            DeliveryStatus status = null;
            // Step 1: En route (simulate travel)
            Thread.sleep(4000);
            box = boxService.updateBoxStateAndBatteryLevel2(box, box.getWeightLimit(),
                    10, BoxState.ENROUTE);

            status = DeliveryStatus.fromName(BoxState.ENROUTE.name());

            boxService.updateDeliveryStatus2(delivery.getDeliveryTrackingNo(), box, status);

            // Step 2: Delivering (offloading)
            Thread.sleep(2000);
            box = boxService.updateBoxStateAndBatteryLevel2(box, box.getWeightLimit(),
                    5, BoxState.DELIVERING);

            status = DeliveryStatus.fromName(BoxState.DELIVERING.name());

            boxService.updateDeliveryStatus2(delivery.getDeliveryTrackingNo(), box, status);

            // Step 3: Delivered
            Thread.sleep(1000);
            box = boxService.updateBoxStateAndBatteryLevel2(box,
                    0, BoxState.DELIVERED);

            status = DeliveryStatus.fromName(BoxState.DELIVERED.name());

            boxService.updateDeliveryStatus2(delivery.getDeliveryTrackingNo(), box, status);

            // Step 4: Returning
            Thread.sleep(4000);
            box = boxService.updateBoxStateAndBatteryLevel2(box,
                    10, BoxState.RETURNING);

            Thread.sleep(1000);
            // Step 5: Idle at depot
            box = boxService.updateBoxStateAndBatteryLevel2(box,
                    3, BoxState.IDLE);

            // clear after delivery
            boxService.resetBox(box);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            boxService.updateDeliveryStatus2(delivery.getDeliveryTrackingNo());
            // clear after error
            boxService.resetBox(box);
            // throw new BoxException("Delivery simulation interrupted", HttpStatus.EXPECTATION_FAILED);
        }

        catch (Exception e) {

            boxService.updateDeliveryStatus2(delivery.getDeliveryTrackingNo());
            // clear after error
            boxService.resetBox(box);
            // throw new BoxException("Delivery simulation interrupted", HttpStatus.EXPECTATION_FAILED);
        }

    }

    public ResponseEntity<?> checkDeliveryStatus(String deliveryTrackingNo){

        Delivery delivery = deliveryRepo.findByDeliveryTrackingNo(deliveryTrackingNo)
                .orElseThrow(() -> new BoxNotFoundException("Tracking number not found",
                        HttpStatus.NOT_FOUND));

        DeliveryStatusDTOResponse deliveryResponse = deliveryStatusMapper
                .deliveryToDeliveryStatusDTOResponse(delivery );

        Map<String, Object> response = new HashMap<>();
        response.put("data", deliveryResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
