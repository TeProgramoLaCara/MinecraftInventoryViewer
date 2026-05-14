package com.joel.inventoryviewerapi.mapper.storageitem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.StorageItem;
import com.joel.inventoryviewerapi.dto.storageitem.StorageItemRequestDTO;
import com.joel.inventoryviewerapi.dto.storageitem.StorageItemResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.item.ItemMapper.class
})
public interface StorageItemMapper {

    StorageItem toEntity(StorageItemRequestDTO dto);

    @Mapping(target = "item", source = "item")
    StorageItemResponseDTO toDTO(StorageItem entity);
}
