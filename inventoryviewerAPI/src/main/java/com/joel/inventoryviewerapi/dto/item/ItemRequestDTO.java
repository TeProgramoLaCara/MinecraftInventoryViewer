package com.joel.inventoryviewerapi.dto.item;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDTO {

    @NotBlank
    private String name;

    private String displayName;

    @NotNull
    private Integer stackSize;

    private Integer maxDurability;

    // JSON en entidad
    private String enchantCategoriesJson;
}

