package com.joel.inventoryviewerapi.mapper.food;

import org.mapstruct.Mapper;

import com.joel.inventoryviewerapi.entity.Food;
import com.joel.inventoryviewerapi.dto.food.FoodRequestDTO;
import com.joel.inventoryviewerapi.dto.food.FoodResponseDTO;

@Mapper(componentModel = "spring")
public interface FoodMapper {

    Food toEntity(FoodRequestDTO dto);

    FoodResponseDTO toDTO(Food entity);
}
