package com.joel.inventoryviewerapi.dto.itemfood;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFoodRequestDTO {

    @NotNull
    private Integer itemId;

    @NotNull
    private Integer foodId;
}

