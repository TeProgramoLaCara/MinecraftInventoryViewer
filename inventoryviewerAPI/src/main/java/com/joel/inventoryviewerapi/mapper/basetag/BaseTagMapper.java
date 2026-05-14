package com.joel.inventoryviewerapi.mapper.basetag;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.joel.inventoryviewerapi.entity.BaseTag;
import com.joel.inventoryviewerapi.dto.basetag.BaseTagResponseDTO;

@Mapper(componentModel = "spring")
public interface BaseTagMapper {

    @Mapping(target = "baseId", source = "base.id")
    BaseTagResponseDTO toDto(BaseTag entity);
}
