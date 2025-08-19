package com.my_company.box_delivery_service.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "delivery_item_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[A-Za-z0-9_-]+$")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double weight;

    @Pattern(regexp = "^[A-Z0-9_]+$")
    @Column(nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)  // FetchType.EAGER
    @JsonBackReference
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


}
