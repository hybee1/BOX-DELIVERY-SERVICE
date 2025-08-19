package com.my_company.box_delivery_service.Controller;


import com.my_company.box_delivery_service.Service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {

        this.deliveryService = deliveryService;
    }

    @GetMapping("/{txRef}/startDelivery")
    public ResponseEntity<?> startDelivery(@PathVariable String txRef) {

        return deliveryService.startDelivery(txRef);
    }

    @GetMapping("/{deliveryTrackingNo}/checkDeliveryStatus")
    public ResponseEntity<?> checkDeliveryStatus(@PathVariable String deliveryTrackingNo) {

        return deliveryService.checkDeliveryStatus(deliveryTrackingNo);
    }


}
