package com.my_company.box_delivery_service.Repo;


import com.my_company.box_delivery_service.Enum.BoxState;
import com.my_company.box_delivery_service.Model.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoxRepo extends JpaRepository<Box, String> {
    List<Box> findByStateAndBatteryCapacityGreaterThanEqual(
            BoxState state, double batteryCapacity);

    Optional<Box> findByTxRefAndState(String txRef, BoxState state);
}
