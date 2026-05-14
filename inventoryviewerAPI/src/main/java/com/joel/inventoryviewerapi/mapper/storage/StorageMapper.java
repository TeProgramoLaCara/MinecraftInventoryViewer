package com.joel.inventoryviewerapi.mapper.storage;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.Storage;
import com.joel.inventoryviewerapi.dto.storage.StorageRequestDTO;
import com.joel.inventoryviewerapi.dto.storage.StorageResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.base.BaseMapper.class,
        com.joel.inventoryviewerapi.mapper.biome.BiomeMapper.class,
        com.joel.inventoryviewerapi.mapper.containertype.ContainerTypeMapper.class,
        com.joel.inventoryviewerapi.mapper.storageitem.StorageItemMapper.class
})
public interface StorageMapper {

    Storage toEntity(StorageRequestDTO dto);

    @Mapping(target = "base", source = "base")
    @Mapping(target = "biome", source = "biome")
    @Mapping(target = "containerType", source = "containerType")
    @Mapping(target = "items", source = "storageItems")
    StorageResponseDTO toDTO(Storage entity);
}
