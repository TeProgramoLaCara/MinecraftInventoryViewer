package com.joel.inventoryviewerapi.mapper.block;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.Block;
import com.joel.inventoryviewerapi.dto.block.BlockRequestDTO;
import com.joel.inventoryviewerapi.dto.block.BlockResponseDTO;

@Mapper(componentModel = "spring", uses = {
        com.joel.inventoryviewerapi.mapper.item.ItemMapper.class
})
public interface BlockMapper {

    Block toEntity(BlockRequestDTO dto);

    @Mapping(target = "item", source = "item")
    BlockResponseDTO toDTO(Block entity);
}
