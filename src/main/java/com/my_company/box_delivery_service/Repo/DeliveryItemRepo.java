package com.my_company.box_delivery_service.Repo;


import com.my_company.box_delivery_service.Model.Delivery;
import com.my_company.box_delivery_service.Model.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DeliveryItemRepo extends JpaRepository<DeliveryItem, Long> {

    Optional<Delivery> findByName(String deliveryTrackingNo);

}