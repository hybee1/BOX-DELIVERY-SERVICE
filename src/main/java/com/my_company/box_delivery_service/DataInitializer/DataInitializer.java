package com.my_company.box_delivery_service.DataInitializer;


import com.my_company.box_delivery_service.Enum.BoxState;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Repo.BoxRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final BoxRepo boxRepo;

    public DataInitializer(BoxRepo boxRepo) {
        this.boxRepo = boxRepo;
    }

    @PostConstruct
    public void init() {
        if (boxRepo.count() == 0) {
            Box box = Box.builder()
                    .txRef("BOX_001")
                    .weightLimit(0)
                    .batteryCapacity(24)
                    .state(BoxState.IDLE)
                    .build();

            boxRepo.save(box);
        }
    }
}

