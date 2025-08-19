package com.my_company.box_delivery_service.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items_tbl")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[A-Za-z0-9_-]+$")
    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Column(nullable = false)
    private double weight = 0.0;

    @Pattern(regexp = "^[A-Z0-9_]+$")
    @Column(nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "box_txRef")
    @JsonBackReference
    private Box box;


}

