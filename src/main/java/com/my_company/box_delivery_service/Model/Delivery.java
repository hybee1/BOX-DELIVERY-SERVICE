package com.my_company.box_delivery_service.Model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.my_company.box_delivery_service.Enum.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString(exclude = "items")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery_tbl")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryTrackingNo;

    private String txRef;

    private double totalItemWeight;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DeliveryItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public void addItem(DeliveryItem item) {
        items.add(item);
        item.setDelivery(this); // keep both sides in sync
    }

    public void removeItem(DeliveryItem item) {
        items.remove(item);
        item.setDelivery(null);
    }

}
