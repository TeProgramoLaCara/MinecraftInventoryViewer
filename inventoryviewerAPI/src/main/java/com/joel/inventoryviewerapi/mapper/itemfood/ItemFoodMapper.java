package com.joel.inventoryviewerapi.mapper.itemfood;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.ItemFood;
import com.joel.inventoryviewerapi.dto.itemfood.ItemFoodRequestDTO;
import com.joel.inventoryviewerapi.dto.itemfood.ItemFoodResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.food.FoodMapper.class
})
public interface ItemFoodMapper {

    ItemFood toEntity(ItemFoodRequestDTO dto);

    @Mapping(target = "food", source = "food")
    ItemFoodResponseDTO toDTO(ItemFood entity);
}
