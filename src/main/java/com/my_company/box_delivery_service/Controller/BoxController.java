package com.my_company.box_delivery_service.Controller;


import com.my_company.box_delivery_service.DTORequest.BoxDTORequest;
import com.my_company.box_delivery_service.DTORequest.ItemDTORequest;
import com.my_company.box_delivery_service.Service.BoxService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/boxes")
public class BoxController {

    private final BoxService boxService;

    public BoxController(BoxService boxService) {
        this.boxService = boxService;
    }

    @PostMapping("/create-box")
    public ResponseEntity<?> createBox(@Valid @RequestBody BoxDTORequest box) {
        return boxService.createBox(box);
    }

    @PostMapping("/{txRef}/load-box")
    public ResponseEntity<?> loadItems(@PathVariable String txRef,
                         @Valid @RequestBody List<ItemDTORequest> items) {
        return boxService.loadItems(txRef, items);
    }

    @GetMapping("/{txRef}/loaded-items")
    public ResponseEntity<?> getLoadedItems(@PathVariable String txRef) {
        return boxService.getItems(txRef);
    }

    @GetMapping("/available-boxes")
    public ResponseEntity<?> getAvailableBoxes() {
        return boxService.getAvailableBoxes();
    }

    @GetMapping("/{txRef}/battery-capacity")
    public ResponseEntity<?> getBatteryLevel(@PathVariable String txRef) {
        return boxService.getBatteryLevel(txRef);
    }
}

