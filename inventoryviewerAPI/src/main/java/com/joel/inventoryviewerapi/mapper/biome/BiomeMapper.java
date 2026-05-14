package com.joel.inventoryviewerapi.mapper.biome;

import org.mapstruct.Mapper;

import com.joel.inventoryviewerapi.entity.Biome;
import com.joel.inventoryviewerapi.dto.biome.BiomeRequestDTO;
import com.joel.inventoryviewerapi.dto.biome.BiomeResponseDTO;

@Mapper(componentModel = "spring")
public interface BiomeMapper {

    Biome toEntity(BiomeRequestDTO dto);

    BiomeResponseDTO toDTO(Biome entity);
}
