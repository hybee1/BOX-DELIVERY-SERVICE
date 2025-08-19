package com.my_company.box_delivery_service.Repo;

import com.my_company.box_delivery_service.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {
    List<Item> findByBox_TxRef(String txRef);
}