package com.joel.inventoryviewerapi.mapper.containertype;

import org.mapstruct.Mapper;

import com.joel.inventoryviewerapi.entity.ContainerType;
import com.joel.inventoryviewerapi.dto.containertype.ContainerTypeRequestDTO;
import com.joel.inventoryviewerapi.dto.containertype.ContainerTypeResponseDTO;

@Mapper(componentModel = "spring")
public interface ContainerTypeMapper {

    ContainerType toEntity(ContainerTypeRequestDTO dto);

    ContainerTypeResponseDTO toDTO(ContainerType entity);
}
