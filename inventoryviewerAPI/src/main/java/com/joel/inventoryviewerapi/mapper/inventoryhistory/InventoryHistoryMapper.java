package com.joel.inventoryviewerapi.mapper.inventoryhistory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.InventoryHistory;
import com.joel.inventoryviewerapi.dto.inventoryhistory.InventoryHistoryRequestDTO;
import com.joel.inventoryviewerapi.dto.inventoryhistory.InventoryHistoryResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.item.ItemMapper.class
})
public interface InventoryHistoryMapper {

    InventoryHistory toEntity(InventoryHistoryRequestDTO dto);

    @Mapping(target = "item", source = "item")
    InventoryHistoryResponseDTO toDTO(InventoryHistory entity);
}
