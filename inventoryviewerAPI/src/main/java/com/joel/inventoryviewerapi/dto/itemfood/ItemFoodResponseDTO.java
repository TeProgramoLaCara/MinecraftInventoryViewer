package com.joel.inventoryviewerapi.dto.itemfood;

import com.joel.inventoryviewerapi.dto.food.FoodResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFoodResponseDTO {

    private Integer id;
    private FoodResponseDTO food;
}

