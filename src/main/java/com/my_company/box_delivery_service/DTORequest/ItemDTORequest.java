package com.my_company.box_delivery_service.DTORequest;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class ItemDTORequest {

    @NotBlank(message = "this field can not be empty and it allows only (letters, " +
            "numbers, hyphen ‘-’ and underscore ‘_’)")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "this field allows only (letters, " +
            "numbers, hyphen ‘-’ and underscore ‘_’)")
    private String name;

    @NotNull(message = "this field can not be empty and please supply weight " +
            "in grams")
    private double weight;

    @NotBlank(message = "this field can not be empty and it allows only " +
            "(UPPERCASE letters, underscore and numbers)")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "this field allows only (UPPERCASE " +
            "letters, underscore and numbers)")
    private String code;

}
