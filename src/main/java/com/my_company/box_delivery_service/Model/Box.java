package com.my_company.box_delivery_service.Model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.my_company.box_delivery_service.Enum.BoxState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "boxes_tbl")
public class Box {

    @Id
    @Column(length = 20)
    private String txRef;

    @Min(value = 0, message = "the box min weight must be 0 grams")
    @Max(value = 500, message = "the box max weight can not exceed 500 grams")
    @Builder.Default
    @Column(nullable = false)
    private double weightLimit = 0;

    @Min(value=0, message = "the box min battery level is 0%")
    @Max(value=100, message = "the box max battery level can not exceed 100%")
    @Builder.Default
    @Column(nullable = false)
    private double batteryCapacity = 100;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoxState state;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference

    private List<Item> items = new ArrayList<>();

}
