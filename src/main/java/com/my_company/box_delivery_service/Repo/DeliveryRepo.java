package com.my_company.box_delivery_service.Repo;


import com.my_company.box_delivery_service.Model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface DeliveryRepo extends JpaRepository<Delivery, String> {

    Optional<Delivery> findByDeliveryTrackingNo(String deliveryTrackingNo);

}