package com.my_company.box_delivery_service.DTOResponse;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.my_company.box_delivery_service.Model.Box;
import com.my_company.box_delivery_service.Model.Item;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ItemDTOResponse {

    private String name;

    private double weight;

    private String code;

    private String boxTxRef;

}
